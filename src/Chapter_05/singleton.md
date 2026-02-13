# 싱글톤 패턴 (Singleton Pattern) 완전 정리

## 1. 문제 상황: 왜 싱글톤이 필요한가?

데이터베이스 커넥션 풀, 로깅, 설정 관리 같은 객체가 애플리케이션 여러 곳에서 `new`로 반복 생성되면 심각한 문제가 발생한다. 동일한 역할의 객체가 메모리에 여러 개 존재하면서 **메모리가 낭비**되고,
한쪽에서 값을 변경해도 다른 쪽에서는 반영되지 않는 **상태 불일치**가 생기며, 파일 I/O나 네트워크 연결 같은 **비용이 큰 초기화가 반복**된다.

```java
// 여기저기서 마구 생성하는 상황
ConfigManager configA = new ConfigManager(); // 설정 로드 (파일 I/O 발생)
ConfigManager configB = new ConfigManager(); // 또 로드...
ConfigManager configC = new ConfigManager(); // 또 로드...
// configA에서 값을 바꿔도 configB는 모른다
```

핵심 니즈는 단 하나다. **"이 객체는 애플리케이션 전체에서 딱 하나만 존재해야 한다."**

---

## 2. 싱글톤 패턴 정의

싱글톤 패턴은 클래스의 인스턴스가 **오직 하나만** 생성되도록 보장하고, 그 인스턴스에 대한 **전역 접근점**을 제공하는 생성(Creational) 패턴이다. 핵심 메커니즘은 세 가지다.

첫째, `private` 생성자로 외부에서 `new`를 못 하게 막는다. 둘째, `static` 필드로 클래스 내부에서 자기 자신의 인스턴스를 보관한다. 셋째, `public static` 메서드인
`getInstance()`를 통해 유일한 인스턴스를 반환한다.

SOLID 원칙과의 관계를 보면, 하나의 인스턴스가 하나의 책임을 지므로 **단일 책임 원칙(SRP)** 과 연관되지만, 전역 상태를 만들기 때문에 **의존 역전 원칙(DIP)** 을 위반하기 쉬우므로 주의가
필요하다.

---

## 3. 구현 방식별 분석

### 3.1 기본 방식 (Thread-Unsafe)

가장 직관적이지만 멀티스레드 환경에서 인스턴스가 2개 이상 생성될 수 있다.

```java
public class ConfigManager {
    private static ConfigManager instance;

    private ConfigManager() {
    }

    public static ConfigManager getInstance() {
        if (instance == null) {             // Thread A, B가 동시에 진입 가능
            instance = new ConfigManager(); // 인스턴스가 2개 생길 수 있음
        }
        return instance;
    }
}
```

Thread A와 B가 동시에 `if (instance == null)` 검사를 통과하면, 각각 새로운 인스턴스를 생성하게 되어 싱글톤의 약속이 깨진다.

### 3.2 synchronized 방식 (간단하지만 느림)

```java
public class ConfigManager {
    private static ConfigManager instance;

    private ConfigManager() {
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
}
```

스레드 안전하지만, 인스턴스가 이미 생성된 이후에도 **매 호출마다 lock**이 걸린다. 인스턴스 생성은 딱 한 번인데 10,000번의 호출 모두에서 lock 비용을 지불하는 셈이다.

### 3.3 Double-Checked Locking (DCL)

```java
public class ConfigManager {
    private static volatile ConfigManager instance; // volatile 필수!

    private ConfigManager() {
    }

    public static ConfigManager getInstance() {
        if (instance == null) {                    // 1차 검사 (lock 없이)
            synchronized (ConfigManager.class) {
                if (instance == null) {             // 2차 검사 (lock 안에서)
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }
}
```

성능과 안전성을 모두 잡으려는 방식이지만, `volatile`을 빠뜨리면 **명령어 재정렬(instruction reordering)** 문제가 발생한다. `instance = new ConfigManager()`는
내부적으로 메모리 할당 → 생성자 실행 → 참조 대입의 3단계인데, JVM이 이 순서를 "메모리 할당 → 참조 대입 → 생성자 실행"으로 재정렬할 수 있다. 이 사이에 다른 스레드가 `instance`를 읽으면 *
*초기화되지 않은 객체**를 사용하게 된다. `volatile` 하나 빠뜨리면 디버깅이 극도로 어려운 버그가 발생하므로 실수 가능성이 높은 방식이다.

명령어 재정렬(Instruction Reordering)이란?
명령어 재정렬이란 CPU와 컴파일러가 성능 최적화를 위해 코드의 실행 순서를 바꾸는 것을 말한다. 우리가 작성한 코드가 위에서 아래로 순서대로 실행될 것이라고 생각하지만, 실제로는 그렇지 않을 수 있다. CPU는
파이프라인 효율을 높이기 위해, JIT 컴파일러는 더 빠른 기계어 코드를 생성하기 위해, 최종 결과가 같다면 중간 실행 순서를 자유롭게 바꿀 수 있다.
단일 스레드에서는 이 재정렬이 문제가 되지 않는다. JVM이 "해당 스레드의 관점에서 결과가 동일하다"는 것을 보장하기 때문이다. 문제는 멀티스레드 환경이다. 한 스레드 내에서 결과가 같더라도, 다른 스레드가 중간
상태를 관찰하면 예상과 다른 값을 읽게 된다. DCL에서 volatile 없이 instance에 참조가 먼저 대입되고 생성자가 나중에 실행되는 상황이 바로 이 케이스다. 작성한 스레드 입장에서는 최종적으로 완성된
객체가 만들어지지만, 다른 스레드는 대입과 생성자 실행 사이의 틈에서 반쯤 만들어진 객체를 보게 되는 것이다.
volatile 키워드는 이 재정렬을 금지하는 메모리 배리어(memory barrier) 역할을 한다. volatile로 선언된 변수에 쓰기가 발생하면, 그 이전의 모든 쓰기 작업이 먼저 완료되는 것이 보장된다.
따라서 instance에 참조가 대입되는 시점에는 생성자가 이미 완료되어 있다는 것을 다른 스레드도 확인할 수 있게 된다.

### 3.4 Bill Pugh Idiom — Lazy Holder (권장)

```java
public class ConfigManager {

    private ConfigManager() {
    }

    // 내부 static 클래스는 getInstance()가 호출될 때 비로소 로드됨
    private static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return Holder.INSTANCE;
    }
}
```

이 방식이 다른 방식들보다 우수한 이유는 **동기화의 책임을 개발자에서 JVM으로 넘겼기 때문**이다. JVM의 클래스 로딩 메커니즘(JLS §12.4.2)이 세 가지를 보장한다. static 필드는 클래스 로딩 시
**딱 한 번만** 초기화되고, JVM 내부적으로 **lock을 사용**하여 동시 접근을 차단하며, 초기화가 완전히 끝난 후에야 다른 스레드가 접근할 수 있다. 개발자가 `synchronized`도,
`volatile`도 신경 쓸 필요가 없다.

#### static 필드와 static 내부 클래스의 차이

여기서 핵심적인 질문이 생긴다. "Holder는 static이니까 처음부터 로딩되는 거 아닌가?" 답은 **아니다**. `static`이라는 키워드의 의미가 맥락에 따라 다르다.

`private static final ConfigManager INSTANCE = new ConfigManager()`처럼 선언된 **static 필드**는 해당 클래스가 로딩될 때 함께 초기화되는 클래스 레벨
변수다. 반면 `private static class Holder`처럼 선언된 **static 내부 클래스**는 JVM이 별도의 독립 클래스로 취급한다. 컴파일하면 `ConfigManager.class`와
`ConfigManager$Holder.class`라는 별도의 .class 파일이 생성된다. `static`은 "외부 클래스의 인스턴스 없이 존재할 수 있다"는 의미일 뿐, "미리 로딩된다"는 뜻이 아니다.

JVM은 게으른(Lazy) 로딩 전략을 사용하여, 클래스는 해당 클래스의 인스턴스를 생성하거나, static 메서드를 호출하거나, static 필드에 접근하는 시점에 **처음 사용될 때** 비로소 초기화된다. 따라서
`Holder.INSTANCE`에 처음 접근하는 `getInstance()` 호출 시점에 비로소 Holder 클래스가 로딩되고, 그때 INSTANCE가 생성되는 것이다.

#### Eager 초기화와의 비교

static 필드에 직접 선언하면(Eager Initialization) 클래스가 로딩되는 순간 바로 생성된다. 생성 비용이 크지 않고 클래스의 역할이 단순하다면 이 방식도 충분히 실용적이다. Bill Pugh
방식이 빛을 발하는 경우는, 외부 클래스에 여러 static 메서드가 있어서 인스턴스 생성과 무관한 이유로 클래스가 먼저 로딩될 수 있고, 인스턴스 생성 비용이 비싼 경우다.

### 3.5 Enum Singleton (가장 안전한 방식)

```java
public enum ConfigManager {
    INSTANCE;

    private final Map<String, String> configs = new HashMap<>();

    public String getConfig(String key) {
        return configs.get(key);
    }

    public void setConfig(String key, String value) {
        configs.put(key, value);
    }
}

// 사용: ConfigManager.INSTANCE.getConfig("db.url");
```

Joshua Bloch(Effective Java 저자)가 가장 추천한 방식이다. JVM이 자체적으로 단일 인스턴스를 보장하므로 **직렬화/역직렬화에 안전**하고, `Constructor.newInstance()`
로도 생성이 불가능하므로 **리플렉션 공격에도 안전**하다.

### 구현 방식 비교 요약

| 구현 방식              | Lazy | Thread-Safe | 리플렉션 방어 | 직렬화 안전 | 추천도   |
|--------------------|------|-------------|---------|--------|-------|
| 기본 (if null)       | O    | X           | X       | X      | ⭐     |
| synchronized       | O    | O           | X       | X      | ⭐⭐    |
| DCL (volatile)     | O    | O           | X       | X      | ⭐⭐⭐   |
| Bill Pugh (Holder) | O    | O           | X       | X      | ⭐⭐⭐⭐  |
| **Enum**           | X    | O           | O       | O      | ⭐⭐⭐⭐⭐ |

---

## 4. Spring의 싱글톤 관리

### 4.1 GoF 싱글톤 vs Spring 싱글톤

GoF 싱글톤은 클래스 자체가 `private static` 필드와 `getInstance()`로 인스턴스를 관리한다. 반면 Spring 싱글톤은 클래스 자신이 싱글톤인지 전혀 모르고, Spring 컨테이너가
외부에서 "이 클래스의 인스턴스는 하나만 만들겠다"고 관리한다.

| 구분      | GoF 싱글톤           | Spring 싱글톤             |
|---------|-------------------|------------------------|
| 관리 주체   | 클래스 자체 (`static`) | Spring IoC 컨테이너        |
| 범위      | JVM 전체에서 1개       | ApplicationContext당 1개 |
| 생성 방식   | `getInstance()`   | 컨테이너가 DI로 주입           |
| 테스트 용이성 | 어려움 (전역 상태)       | 쉬움 (Mock 주입 가능)        |

### 4.2 DefaultSingletonBeanRegistry: 싱글톤 저장소

Spring의 `ApplicationContext`는 내부적으로 `DefaultListableBeanFactory`를 가지고 있고, 이 클래스는 `DefaultSingletonBeanRegistry`를 상속한다.

```
ApplicationContext
  └→ BeanFactory
       └→ DefaultListableBeanFactory
            └→ extends DefaultSingletonBeanRegistry  ← 싱글톤 관리의 핵심
```

핵심 코드를 단순화하면 다음과 같다.

```java
public class DefaultSingletonBeanRegistry {

    // 싱글톤 Bean을 저장하는 Map: key = Bean 이름, value = Bean 인스턴스
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
        }
    }

    public Object getSingleton(String beanName) {
        return this.singletonObjects.get(beanName);
    }
}
```

Spring의 싱글톤 관리란 결국 **`ConcurrentHashMap`에 Bean 이름을 key로, 인스턴스를 value로 저장하고 꺼내는 것**이다. GoF처럼 각 클래스가 자기 인스턴스를 `static` 필드로
보관하는 것이 아니라, 중앙 Registry(Map)에서 모든 싱글톤을 한 곳에 모아 관리한다.

### 4.3 Bean 생성 흐름

애플리케이션 시작 시 `ApplicationContext`가 생성되면서 싱글톤 스코프의 Bean을 전부 미리 생성한다(Eager Initialization).

```
SpringApplication.run() 호출
  └→ ApplicationContext 생성
       └→ Bean 정의(BeanDefinition) 수집
            └→ 싱글톤 Bean 전부 생성 시작
                 ├→ singletonObjects.get("paymentService") → null → 생성 → put
                 ├→ singletonObjects.get("orderService") → null → 생성 → put
                 └→ 의존성 주입 완료
       └→ ApplicationReadyEvent 발행
```

이후 `@Autowired`나 `getBean()`으로 Bean을 요청하면 Map에서 꺼내서 반환할 뿐, 새로 만들지 않는다.

### 4.4 Eager vs Lazy 초기화

Spring이 기본으로 Eager 초기화를 선택한 이유는 두 가지다. 첫째, 커넥션 풀이나 스레드 풀처럼 처음부터 준비되어야 하는 인프라 Bean이 많기 때문이고, 둘째, 설정 오류를 시작 시점에 빠르게 발견(
Fail-Fast)할 수 있기 때문이다.

모든 Bean이 시작 시점에 필요한 건 아니므로, Spring은 `@Lazy` 어노테이션도 제공한다. `@Lazy`가 붙은 Bean은 Spring이 실제 객체 대신 **Proxy 객체**를 먼저 주입하고, 실제
메서드가 호출되는 순간 비로소 진짜 Bean을 생성한다.

```java
@Service
@Lazy  // 실제 사용 시점까지 생성 지연
public class AdminReportService { ...
}
```

Spring Boot에서는 `spring.main.lazy-initialization=true`로 전체 Lazy 모드를 켤 수도 있지만, 운영 환경에서는 Bean 설정 오류가 실제 요청 시점에 터질 수 있으므로
주의가 필요하다. 인프라 Bean에는 `@Lazy(false)`로 Eager를 유지하는 것이 안전하다.

### 4.5 순환 참조와 3단계 캐시

두 Bean이 서로를 의존하는 순환 참조 상황을 해결하기 위해 Spring은 3단계 캐시 구조를 사용한다.

```java
public class DefaultSingletonBeanRegistry {
    // 1단계: 완성된 싱글톤 Bean 저장소
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    // 2단계: 미완성이지만 참조 가능한 Bean (프록시 포함)
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
    // 3단계: Bean을 만들어낼 수 있는 팩토리
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
}
```

핵심 전략은 "미완성 상태의 참조를 먼저 건네주고, 나중에 완성한다"는 것이다. Java에서 객체 변수는 참조(reference)를 저장하므로, 미완성 시점에 전달한 참조와 완성 후의 참조가 가리키는 객체는
동일하다.

다만 이 3단계 캐시는 필드 주입(`@Autowired`)이나 setter 주입에서만 동작하고, 생성자 주입에서는 순환 참조가 에러를 발생시킨다. Spring Boot 2.6부터는 순환 참조 자체가 기본적으로
금지되었는데, 순환 참조가 필요한 상황 자체가 설계의 문제일 가능성이 높기 때문이다.

---

## 5. 싱글톤의 실무 사례

싱글톤이 적합한 대상에는 공통점이 있다. 인스턴스가 여러 개 존재하면 상태 불일치나 리소스 낭비가 발생하는 객체들이다.

**Logger** — SLF4J의 `LoggerFactory`는 내부적으로 `ConcurrentHashMap`에 이름별로 Logger를 캐싱한다. 같은 이름으로 `getLogger()`를 여러 번 호출해도 동일한
인스턴스가 반환된다. 엄밀히는 이름별로 하나씩 존재하는 "멀티톤(Multiton)" 패턴이지만, 핵심 원리는 싱글톤과 동일하다.

**Runtime** — `java.lang.Runtime.getRuntime()`은 GoF 싱글톤의 교과서적 예이다. JVM 환경은 물리적으로 하나이므로 이를 표현하는 객체도 하나여야 한다. Eager 초기화
방식을 사용하는데, JVM 환경은 처음부터 존재해야 하므로 Lazy가 불필요하기 때문이다.

**CacheManager** — 캐시의 존재 이유 자체가 "한 곳에 저장해두고 여러 곳에서 공유"하는 것이므로 싱글톤과 잘 맞는다. CacheManager가 여러 개 존재하면 A에서 저장한 데이터를 B에서 찾지
못하는 상태 불일치가 발생한다.

**Environment** — Spring의 `Environment` 객체는 설정값의 단일 진실 공급원(Single Source of Truth) 역할을 한다. 여러 인스턴스가 존재하면 런타임 중 설정 변경 시
일부 인스턴스만 새 값을 가지는 문제가 생긴다.

**ApplicationContext** — 모든 싱글톤 Bean을 담고 있는 컨테이너 자체가 싱글톤이다. "싱글톤들을 관리하는 객체가 싱글톤"이라는 메타적 구조이다.

**팩토리 패턴과의 결합** — 객체를 생성하는 Factory 자체는 여러 개일 필요가 없으므로 싱글톤으로 만드는 것이 자연스럽다. Spring의 `BeanFactory`가 바로 "싱글톤인 팩토리"이다.

판단 기준은 간단하다. **"이 객체가 2개 존재한다면 말이 되는가?"** 말이 안 되면 싱글톤, 말이 되면 싱글톤이 아니다.

---

## 6. 싱글톤의 테스트 문제

### 6.1 왜 테스트가 어려운가?

GoF 싱글톤의 가장 큰 약점은 테스트 용이성이다. 좋은 단위 테스트의 핵심은 "테스트 대상만 격리하여 테스트한다"는 것인데, GoF 싱글톤은 이 격리를 구조적으로 어렵게 만든다.

```java
public class OrderService {
    public Order createOrder(String item, int price) {
        // 싱글톤에 직접 의존 — 이 연결고리를 끊을 수 없음
        PaymentResult result = PaymentGateway.getInstance().processPayment(price);
        if (result.isSuccess()) {
            return new Order(item, price, "COMPLETED");
        }
        return new Order(item, price, "FAILED");
    }
}
```

이 코드를 테스트하면 `PaymentGateway`의 실제 결제 API가 호출되어 진짜 돈이 빠져나간다. `PaymentGateway`를 가짜(Mock)로 교체하고 싶지만, GoF 싱글톤에서는 이것이 구조적으로
불가능하다.

세 가지 근본 원인이 있다. 첫째, `getInstance()`라는 **정적 메서드 호출은 오버라이드할 수 없다.** Java에서 `static` 메서드는 클래스에 바인딩되므로 다형성이 적용되지 않는다. 둘째, *
*의존성이 코드 내부에 하드코딩**되어 있어서 생성자나 메서드 시그니처만 봐서는 `PaymentGateway`에 의존한다는 사실이 드러나지 않는다. 셋째, **인스턴스가 JVM 레벨에서 고정**되어 있어 테스트마다
새로운 상태로 초기화하는 것도 불가능하다.

### 6.2 테스트 간 상태 오염 문제

싱글톤이 상태를 가지고 있을 때, 한 테스트에서 변경한 상태가 다음 테스트에 영향을 미친다.

```java
@Test
void 첫번째_테스트() {
    ShoppingCart.getInstance().addItem("노트북");
    assertEquals(1, ShoppingCart.getInstance().getItemCount()); // 통과
}

@Test
void 두번째_테스트() {
    ShoppingCart.getInstance().addItem("키보드");
    // 실패! 이전 테스트의 "노트북"이 남아있어 실제값은 2
    assertEquals(1, ShoppingCart.getInstance().getItemCount());
}
```

같은 인스턴스가 모든 테스트에서 공유되므로 테스트 실행 순서에 따라 결과가 달라진다.

### 6.3 해결: 의존성 주입 (DI)

해결 방법은 싱글톤에 직접 접근하는 것을 그만두고, 인터페이스를 통해 **외부에서 의존성을 주입받는 구조**로 바꾸는 것이다.

```java
// 인터페이스로 추상화
public interface PaymentGateway {
    PaymentResult processPayment(int amount);
}

// OrderService는 인터페이스에만 의존
@Service
public class OrderService {
    private final PaymentGateway paymentGateway;

    public OrderService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway; // 외부에서 주입
    }

    public Order createOrder(String item, int price) {
        PaymentResult result = paymentGateway.processPayment(price);
        if (result.isSuccess()) {
            return new Order(item, price, "COMPLETED");
        }
        return new Order(item, price, "FAILED");
    }
}
```

이제 테스트에서 Mock을 자유롭게 주입할 수 있다.

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private OrderService orderService;

    @Test
    void 결제_성공시_주문상태는_COMPLETED() {
        given(paymentGateway.processPayment(anyInt()))
                .willReturn(PaymentResult.success());

        Order order = orderService.createOrder("노트북", 1_500_000);

        assertEquals("COMPLETED", order.getStatus());
        // 실제 결제 API 호출 없음, 네트워크 불필요, 결과를 내가 제어
    }
}
```

이것이 **의존 역전 원칙(DIP)** 이다. `OrderService`(고수준 모듈)가 `ExternalPaymentGateway`(저수준 모듈)에 직접 의존하지 않고, 둘 다 인터페이스(추상화)에 의존한다.
Spring은 이 구조를 컨테이너를 통해 자동으로 연결해 준다.

모든 문제의 근본 원인은 "객체가 자신의 생명주기를 스스로 관리한다"는 점이고, Spring이 선택한 해법은 이 관리 책임을 클래스에서 빼앗아 컨테이너에게 넘기는 것이다.

---

## 7. 주의사항 및 한계

### Anti-Pattern 경고

싱글톤이 많아지면 결국 전역 변수와 다를 바 없어 코드 추적이 어려워진다. `getInstance()`에 직접 의존하면 Mock으로 교체하기 힘들고, 생성자에 드러나지 않고 메서드 내부에서 호출하면 의존 관계가 숨겨진다.

### 사용하지 말아야 할 때

상태가 자주 변경되는 객체(경합 조건 발생), 테스트 격리가 중요한 환경, Spring 같은 DI 컨테이너를 이미 사용 중일 때는 GoF 싱글톤을 직접 구현하지 않는 것이 좋다.

### 사용이 적절한 경우

순수 Java 환경에서 불변(immutable) 설정값을 관리하거나, 로깅이나 캐시 같은 인프라성 객체를 다루거나, Framework 없이 가벼운 유틸리티 클래스를 작성할 때가 적합하다.

### 실무 핵심 원칙

Spring을 사용하고 있다면 GoF 싱글톤 패턴을 직접 구현할 일은 거의 없다. `@Service`, `@Component` 등을 붙이면 Spring 컨테이너가 싱글톤 관리, 스레드 안전성, 의존성 주입을 모두
처리해 준다.