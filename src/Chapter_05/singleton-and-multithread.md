# 싱글톤과 멀티스레드의 깊은 관계 — Java Memory Model과 happens-before로 이해하기

이 문서는 [singleton.md](singleton.md), [why-enum-singleton.md](why-enum-singleton.md)에 이은 심화편으로, 싱글톤이 멀티스레드 환경에서 직면하는 두 가지
근본 문제 — **생성 시점의 안전성**과 **사용 시점의 안전성** — 을 Java Memory Model(JMM)의 관점에서 깊이 있게 다룬다.

---

## 1. 왜 싱글톤과 멀티스레드는 뗄 수 없는 관계인가?

싱글톤은 본질적으로 "여러 곳에서 하나의 인스턴스를 공유한다"는 패턴이다. 그런데 "여러 곳"이 멀티스레드 환경에서는 "여러 스레드"가 된다. 하나의 객체를 여러 스레드가 동시에 접근하는 순간, 두 가지 근본적인
문제가 발생한다.

첫째, **생성 시점의 문제** — 여러 스레드가 동시에 `getInstance()`를 호출하면 인스턴스가 2개 만들어질 수 있다. 이것은 `singleton.md`에서 다룬 내용이다.

둘째, **사용 시점의 문제** — 싱글톤이 상태(필드)를 가지고 있을 때, 한 스레드가 변경한 값을 다른 스레드가 못 볼 수 있다. 이것이 이 문서의 핵심 주제다.

두 번째 문제를 이해하려면 **Java Memory Model(JMM)** 을 알아야 한다.

---

## 2. Java Memory Model: 스레드는 같은 세상을 보지 않는다

우리는 보통 "변수에 값을 쓰면 다른 곳에서도 바로 보이겠지"라고 생각한다. 단일 스레드에서는 맞는 말이지만, 멀티스레드에서는 틀리다.

### 2.1 하드웨어 레벨: CPU 캐시의 존재

현대 CPU는 메인 메모리(RAM)에 직접 접근하지 않는다. 성능을 위해 각 CPU 코어마다 자체 캐시(L1, L2)를 가지고 있고, 대부분의 읽기/쓰기는 이 캐시에서 일어난다.

```
┌─────────────┐     ┌─────────────┐
│   Thread A  │     │   Thread B  │
│ (CPU Core 1)│     │ (CPU Core 2)│
└──────┬──────┘     └──────┬──────┘
       │                   │
┌──────▼──────┐     ┌──────▼──────┐
│  L1 Cache   │     │  L1 Cache   │
│ count = 5   │     │ count = 0   │  ← 같은 변수인데 다른 값!
└──────┬──────┘     └──────┬──────┘
       │                   │
       └────────┬──────────┘
         ┌──────▼──────┐
         │ Main Memory │
         │  count = 5  │
         └─────────────┘
```

Thread A가 `count = 5`로 변경했지만, 이 값은 Core 1의 캐시에만 반영되어 있을 수 있다. Thread B는 Core 2의 캐시에서 여전히 옛날 값인 `0`을 읽게 된다. 이것이 **가시성(
visibility) 문제**이다.

### 2.2 JMM의 추상화: 워킹 메모리

JMM은 이 하드웨어 현실을 추상화한다. 각 스레드가 **워킹 메모리(Working Memory)** 라는 자기만의 작업 공간을 가지고, 메인 메모리의 변수를 복사해 와서 사용한다고 모델링한다.

```java
public class Singleton {
    private static Singleton instance;  // 메인 메모리에 존재
    private int value = 0;              // 메인 메모리에 존재
}
```

```
Thread A의 워킹 메모리              Thread B의 워킹 메모리
┌─────────────────┐              ┌─────────────────┐
│ instance = 0x1A │              │ instance = null  │ ← 아직 못 봄!
│ value = 42      │              │ value = ???      │
└────────┬────────┘              └────────┬────────┘
         │          ┌──────────┐          │
         └──────────┤   Main   ├──────────┘
                    │  Memory  │
                    │ instance │
                    │ = 0x1A   │
                    └──────────┘
```

Thread A가 `instance`를 생성하고 `value`를 42로 설정했지만, 이 변경이 메인 메모리로 언제 플러시(flush)되고, Thread B가 언제 갱신(refresh)할지는 JMM이 보장하지 않는다.
명시적인 동기화 없이는 Thread B가 영원히 `null`을 볼 수도 있다.

---

## 3. 가시성 문제를 코드로 확인하기

실제로 가시성 문제가 발생하는 코드를 보자.

```java
public class VisibilityProblem {

    // volatile 없이 선언된 플래그
    private static boolean ready = false;
    private static int number = 0;

    public static void main(String[] args) throws InterruptedException {

        // Reader 스레드: ready가 true가 되길 기다림
        Thread reader = new Thread(() -> {
            while (!ready) {
                // busy-wait: ready가 true가 될 때까지 반복
                // JIT 컴파일러가 이 루프를 최적화할 수 있음!
            }
            System.out.println("number = " + number);
        });

        reader.start();

        // 잠시 대기 후 Writer 역할 수행
        Thread.sleep(100);
        number = 42;
        ready = true;
        System.out.println("Main: ready를 true로 설정함");
    }
}
```

이 코드의 기대 동작은 "reader 스레드가 `ready = true`를 감지하고 `number = 42`를 출력"하는 것이다. 하지만 실제로는 세 가지 다른 결과가 나올 수 있다.

정상적으로 `number = 42`가 출력될 수도 있지만, reader 스레드가 자신의 워킹 메모리에서 `ready`의 옛날 값(`false`)만 계속 읽어서 **무한 루프에 빠질 수도** 있다. 더 기묘한
경우로는, `ready = true`는 보이지만 `number`의 변경은 아직 안 보여서 `number = 0`이 출력되는 상황도 가능하다. 명령어 재정렬에 의해 `ready = true`가 `number = 42`
보다 먼저 메인 메모리에 반영될 수 있기 때문이다.

이것이 싱글톤에서도 중요한 이유는, DCL 싱글톤에서 `volatile` 없이 구현하면 정확히 같은 문제가 발생하기 때문이다. Thread A가 인스턴스를 생성하고 필드를 초기화했지만, Thread B는 "인스턴스
참조는 보이는데 내부 필드는 초기화 안 된" 상태를 볼 수 있다.

---

## 4. happens-before: JMM의 질서 규칙

JMM은 이 혼란 속에서 질서를 잡기 위해 **happens-before 관계**라는 규칙을 정의한다. "A happens-before B"라는 것은, A에서 수행한 모든 쓰기 작업이 B에서 반드시 보인다는
보장이다.

### 4.1 synchronized의 happens-before

```java
public class SynchronizedSingleton {
    private static SynchronizedSingleton instance;
    private int value;

    public static synchronized SynchronizedSingleton getInstance() {
        if (instance == null) {
            instance = new SynchronizedSingleton();
            instance.value = 42;
        }
        return instance;
    }
}
```

`synchronized` 블록의 happens-before 규칙은 다음과 같다. 한 스레드가 synchronized 블록을 나가면(unlock), 그 안에서 수행한 모든 쓰기가 같은 lock으로
synchronized 블록에 진입하는(lock) 다음 스레드에게 보인다.

```
Thread A                              Thread B
━━━━━━━━━━━━━━━━━━                   ━━━━━━━━━━━━━━━━━━
lock 획득
  instance = new ...
  instance.value = 42
lock 해제  ─── happens-before ───→    lock 획득
                                       instance를 읽음 (non-null)
                                       value를 읽음 (42 보장!)
                                     lock 해제
```

이것이 `synchronized` 방식의 싱글톤이 스레드 안전한 이유다. 단순히 "한 번에 하나만 진입"시키는 것뿐만 아니라, 메모리 가시성까지 보장한다. 단, 인스턴스가 이미 생성된 후에도 매번 lock을 획득해야
한다는 성능 비용이 있다.

### 4.2 volatile의 happens-before

```java
public class DCLSingleton {
    private static volatile DCLSingleton instance;
    private int value;

    public static DCLSingleton getInstance() {
        if (instance == null) {                      // volatile 읽기 (1차 검사)
            synchronized (DCLSingleton.class) {
                if (instance == null) {               // 2차 검사
                    DCLSingleton temp = new DCLSingleton();
                    temp.value = 42;
                    instance = temp;                  // volatile 쓰기
                }
            }
        }
        return instance;                              // volatile 읽기
    }
}
```

`volatile`의 happens-before 규칙은 다음과 같다. volatile 변수에 대한 쓰기가, 그 volatile 변수를 읽는 다른 스레드에서의 읽기보다 happens-before이다. 그리고
핵심적으로, volatile 쓰기 이전에 수행된 **모든 쓰기 작업**(volatile이 아닌 변수 포함)까지 함께 가시성이 보장된다.

```
Thread A                              Thread B
━━━━━━━━━━━━━━━━━━                   ━━━━━━━━━━━━━━━━━━
temp = new DCLSingleton()
temp.value = 42
instance = temp  ─── happens-before ───→  instance를 읽음
  (volatile 쓰기)                           (volatile 읽기)
                                          value를 읽음 (42 보장!)
```

`volatile` 쓰기가 일종의 "울타리(fence)" 역할을 한다. 이 울타리 이전의 모든 변경 사항이 울타리를 넘어 다른 스레드에게 보이게 된다. 그래서 `instance`만 `volatile`로 선언해도,
`value = 42`라는 non-volatile 쓰기까지 함께 가시성이 보장되는 것이다.

### 4.3 클래스 초기화의 happens-before

```java
public class BillPughSingleton {
    private int value;

    private BillPughSingleton() {
        this.value = 42;
    }

    private static class Holder {
        static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

JLS §12.4.2에 의해 클래스 초기화가 완료되면, 그 클래스를 사용하는 모든 스레드에 대해 happens-before 관계가 성립한다. Holder 클래스의 초기화가 완료되면(`INSTANCE` 생성과
`value = 42` 포함), 이후 `Holder.INSTANCE`에 접근하는 어떤 스레드든 완성된 객체를 보게 된다.

```
JVM 클래스 로더
━━━━━━━━━━━━━━━━━━━━━━━━━━━
Holder 클래스 초기화 시작
  (JVM 내부 lock 획득)
  INSTANCE = new BillPughSingleton()
  value = 42
Holder 클래스 초기화 완료
  (JVM 내부 lock 해제)
     │
     │ happens-before
     ▼
  모든 스레드에서 INSTANCE와 value가 정상적으로 보임
```

이것이 Bill Pugh 방식에서 `volatile`도 `synchronized`도 필요 없는 진짜 이유다. 개발자가 직접 happens-before를 만들 필요 없이, JVM의 클래스 초기화 스펙 자체가
happens-before를 보장하기 때문이다.

---

## 5. 세 가지 방식의 happens-before 비교

`synchronized` 방식은 **lock/unlock**으로 happens-before를 만든다. 가장 직관적이고 확실하지만, 매 호출마다 lock 비용이 발생하는 것이 대가이다.

`volatile` 방식(DCL)은 **volatile 변수의 쓰기/읽기**로 happens-before를 만든다. lock보다 가볍지만, 개발자가 `volatile` 선언을 잊으면 가시성 보장이 사라지는 위험이
있다.

**클래스 초기화**(Bill Pugh) 방식은 **JVM의 클래스 로딩 메커니즘**이 happens-before를 만든다. 개발자가 동기화를 신경 쓸 필요가 전혀 없고, JVM이 내부적으로 최적의 방식으로
처리한다.

---

## 6. 싱글톤이 상태를 가질 때: 진짜 위험한 영역

지금까지는 "생성 시점"의 스레드 안전성을 다뤘다. 하지만 실무에서 더 자주 문제가 되는 것은, 이미 생성된 싱글톤의 상태를 여러 스레드가 동시에 수정할 때다. 싱글톤이 안전하게 생성되었다 하더라도, 그 이후의
사용이 스레드 안전하다는 보장은 없다.

### 6.1 위험한 싱글톤: 동기화 없는 상태 변경

```java
public enum UserSessionManager {
    INSTANCE;

    // HashMap은 thread-safe하지 않다!
    private final Map<String, UserSession> sessions = new HashMap<>();

    public void addSession(String sessionId, UserSession session) {
        sessions.put(sessionId, session);  // 동시 접근 시 데이터 손실
    }

    public UserSession getSession(String sessionId) {
        return sessions.get(sessionId);    // 동시 수정 중 읽으면 무한 루프 가능
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
```

Enum 싱글톤이라 생성은 완벽하게 안전하지만, 내부의 `HashMap`은 전혀 스레드 안전하지 않다. 여러 스레드가 동시에 `put()`을 호출하면 데이터가 유실되거나, 최악의 경우 `HashMap`의 내부 구조가
깨져서 `get()` 호출 시 무한 루프에 빠질 수 있다. 이것은 Java 7 이전의 `HashMap`에서 실제로 발생했던 유명한 버그다.

### 6.2 해결: ConcurrentHashMap과 원자적 복합 연산

가장 간단한 방법은 `ConcurrentHashMap`을 사용하는 것이다. 이 클래스는 내부적으로 세분화된 lock(bucket-level locking)을 사용하여 높은 동시성을 제공한다.

```java
public enum UserSessionManager {
    INSTANCE;

    // ConcurrentHashMap: 내부적으로 thread-safe
    private final Map<String, UserSession> sessions = new ConcurrentHashMap<>();

    public void addSession(String sessionId, UserSession session) {
        sessions.put(sessionId, session);  // 안전
    }

    public UserSession getSession(String sessionId) {
        return sessions.get(sessionId);    // 안전
    }
}
```

하지만 개별 연산이 스레드 안전하다고 해서, **복합 연산(compound action)**까지 안전한 것은 아니다.

```java
// Check-Then-Act: 두 연산 사이에 다른 스레드가 끼어들 수 있다!
public void addSessionIfAbsent(String sessionId, UserSession session) {
    if (!sessions.containsKey(sessionId)) {   // 검사
        // 이 사이에 다른 스레드가 같은 sessionId로 put 할 수 있음!
        sessions.put(sessionId, session);       // 행동
    }
}
```

`containsKey()`와 `put()` 각각은 스레드 안전하지만, 두 연산을 합친 "없으면 넣어라"는 원자적(atomic)이지 않다. 이럴 때는 `ConcurrentHashMap`이 제공하는 원자적 복합 연산을
사용해야 한다.

```java
// 원자적 복합 연산 사용
public void addSessionIfAbsent(String sessionId, UserSession session) {
    sessions.putIfAbsent(sessionId, session);  // 검사와 삽입이 하나의 원자적 연산
}

// 더 복잡한 로직이 필요하면 compute 사용
public void updateOrCreateSession(String sessionId, UserSession newSession) {
    sessions.compute(sessionId, (key, existing) -> {
        if (existing == null) {
            return newSession;           // 없으면 새로 생성
        }
        existing.refresh();              // 있으면 갱신
        return existing;
    });
    // compute 내부의 람다 전체가 원자적으로 실행됨
}
```

### 6.3 또 다른 접근: 불변(Immutable) 싱글톤

동시성 문제를 다루는 가장 근본적인 해결책은 **상태를 아예 변경하지 않는 것**이다. 불변 객체는 생성 이후 상태가 바뀌지 않으므로, 어떤 스레드가 언제 읽어도 항상 같은 값을 보게 된다. 동기화가 전혀 필요
없다.

```java
public enum AppConfig {
    INSTANCE;

    // 모든 필드가 final이고, 생성 이후 변경 불가
    private final String dbUrl;
    private final int maxPoolSize;
    private final boolean debugMode;

    AppConfig() {
        // 생성 시점에 모든 값을 확정
        this.dbUrl = loadFromFile("db.url");
        this.maxPoolSize = Integer.parseInt(loadFromFile("pool.maxSize"));
        this.debugMode = Boolean.parseBoolean(loadFromFile("debug.mode"));
    }

    // getter만 제공, setter 없음
    public String getDbUrl() {
        return dbUrl;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    private String loadFromFile(String key) {
        // 설정 파일에서 값 로드
        return "";
    }
}
```

이 싱글톤은 `volatile`도 `synchronized`도 `ConcurrentHashMap`도 필요 없다. 불변이기 때문에 happens-before를 고민할 필요 자체가 없다.

만약 설정값이 변경 가능해야 한다면, 기존 객체를 수정하는 대신 새로운 불변 객체를 만들어 교체하는 방식을 사용할 수 있다.

```java
public class ConfigHolder {

    // volatile 참조: 교체 시점의 가시성을 보장
    private static volatile AppConfig current = AppConfig.load();

    public static AppConfig get() {
        return current;  // 불변 객체를 반환 — 읽는 쪽은 동기화 불필요
    }

    public static void refresh() {
        current = AppConfig.load();  // 새로운 불변 객체로 교체 (기존 객체는 수정 안 함)
    }
}
```

`current` 참조만 `volatile`로 선언하면, 교체 시점의 가시성은 보장되면서도 `AppConfig` 객체 자체는 불변이므로 읽기 작업에 어떤 동기화도 필요 없다. 읽기가 압도적으로 많고 변경이 드문
설정값 같은 경우에 매우 효과적인 패턴이다.

---

## 7. 실무 핵심 정리

싱글톤과 멀티스레드의 관계는 결국 두 가지 질문으로 귀결된다.

첫 번째 질문은 **"생성이 안전한가?"** 이다. 여러 스레드가 동시에 `getInstance()`를 호출할 때 인스턴스가 하나만 만들어지는지의 문제다. 이것은 구현 방식 선택의 문제이고, Bill Pugh나
Enum 방식을 사용하면 JVM이 보장한다.

두 번째 질문은 **"사용이 안전한가?"** 이다. 이미 생성된 싱글톤의 상태를 여러 스레드가 동시에 읽고 쓸 때 데이터 정합성이 유지되는지의 문제다. 이것은 구현 방식으로 해결되지 않고, 싱글톤이 관리하는 내부
상태에 대한 동시성 전략이 필요하다.

두 번째 질문에 대한 전략은 세 가지로 나뉜다. 상태를 아예 없애거나(stateless), 상태를 불변으로 만들거나(immutable), 동기화된 자료구조를 사용하는 것이다. Spring의 `@Service`가 보통 첫 번째 방식으로, 비즈니스 로직만 있고 인스턴스 변수에 상태를 저장하지 않는다. 생성 이후 변경이 없으면 동기화 자체가 불필요하고, 가변 상태가 불가피하다면
`ConcurrentHashMap`, `AtomicReference` 같은 도구를 사용한다.

결국 싱글톤을 설계할 때 가장 먼저 해야 할 질문은 "이 싱글톤이 상태를 가져야 하는가?"이고, 가능하다면 상태를 갖지 않는 것이 최선이다. 상태가 필요하다면 불변을 우선 고려하고, 가변 상태가 불가피할 때만 동기화
전략을 도입하는 것이 올바른 사고 순서다.