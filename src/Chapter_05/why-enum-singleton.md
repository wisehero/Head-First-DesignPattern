# 왜 Enum 싱글톤인가? — 리플렉션과 직렬화로 알아보는 싱글톤의 취약점

이 문서는 [singleton.md](singleton.md)의 심화편으로, 싱글톤의 "인스턴스는 하나"라는 약속이 어떻게 깨질 수 있는지, 그리고 왜 Enum만이 이를 완벽하게 방어하는지를 코드와 함께 살펴본다.

---

## 1. 리플렉션으로 싱글톤 깨뜨리기

Java의 리플렉션 API는 `private` 접근 제어자를 무시할 수 있는 강력한 기능이다. 싱글톤의 핵심 방어 수단이 `private` 생성자인데, 리플렉션은 이 방어벽을 그냥 뚫어버린다.

### 1.1 Bill Pugh 방식에 대한 공격

앞선 문서에서 "권장 방식"이라고 했던 Bill Pugh Idiom이 어떻게 깨지는지 봅시다.

```java
public class ConfigManager {

    private ConfigManager() {
        System.out.println("ConfigManager 생성자 호출!");
    }

    private static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return Holder.INSTANCE;
    }
}
```

```java
import java.lang.reflect.Constructor;

public class ReflectionAttack {

    public static void main(String[] args) throws Exception {
        // 1. 정상적인 방법으로 싱글톤 인스턴스 획득
        ConfigManager instance1 = ConfigManager.getInstance();

        // 2. 리플렉션으로 private 생성자에 접근
        Constructor<ConfigManager> constructor =
                ConfigManager.class.getDeclaredConstructor();

        // private 접근 제어를 무력화!
        constructor.setAccessible(true);

        // 3. 강제로 두 번째 인스턴스 생성
        ConfigManager instance2 = constructor.newInstance();

        // 4. 결과 확인
        System.out.println("instance1: " + instance1.hashCode());
        System.out.println("instance2: " + instance2.hashCode());
        System.out.println("같은 객체인가? " + (instance1 == instance2));
    }
}
```

실행 결과는 다음과 같다.

```
ConfigManager 생성자 호출!
ConfigManager 생성자 호출!        ← 생성자가 두 번 호출됨
instance1: 1234567890
instance2: 987654321              ← 해시코드가 다름
같은 객체인가? false              ← 싱글톤이 깨졌다
```

`setAccessible(true)` 한 줄이면 `private` 접근 제어 자체가 무력화된다. Bill Pugh, DCL, synchronized 방식 모두 이 공격에 동일하게 취약하다.

### 1.2 방어 시도: 생성자 내부에서 막기

생성자 안에서 "이미 인스턴스가 존재하면 예외를 던지는" 방어 코드를 넣어볼 수 있다.

```java
public class ConfigManager {

    private static boolean isCreated = false;

    private ConfigManager() {
        if (isCreated) {
            throw new IllegalStateException(
                    "싱글톤 객체는 이미 생성되었습니다! 리플렉션으로 생성할 수 없습니다."
            );
        }
        isCreated = true;
        System.out.println("ConfigManager 생성!");
    }

    private static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return Holder.INSTANCE;
    }
}
```

리플렉션으로 두 번째 인스턴스를 만들려고 하면 예외가 발생한다.

```
ConfigManager 생성!
Exception in thread "main" java.lang.IllegalStateException:
    싱글톤 객체는 이미 생성되었습니다! 리플렉션으로 생성할 수 없습니다.
```

방어가 된 것처럼 보이지만, `isCreated`라는 플래그 자체도 리플렉션으로 조작할 수 있다.

```java
// isCreated 플래그마저 리플렉션으로 무력화
Field field = ConfigManager.class.getDeclaredField("isCreated");
field.setAccessible(true);
field.set(null, false);  // false로 되돌림

// 이제 다시 생성 가능
ConfigManager instance2 = constructor.newInstance(); // 성공
```

리플렉션이라는 도구 자체가 Java의 모든 접근 제어를 우회할 수 있도록 설계되어 있기 때문에, 일반 클래스로는 완벽한 방어가 구조적으로 불가능하다. 어떤 방어 코드를 넣어도 리플렉션으로 다시 무력화할 수 있는 끝없는 추격전이 된다.

### 1.3 Enum은 왜 리플렉션에 안전한가?

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
```

같은 리플렉션 공격을 Enum에 시도해 보자.

```java
public class EnumReflectionAttack {

    public static void main(String[] args) throws Exception {
        ConfigManager instance1 = ConfigManager.INSTANCE;

        // Enum의 생성자는 내부적으로 (String name, int ordinal) 파라미터를 받음
        Constructor<ConfigManager> constructor =
                ConfigManager.class.getDeclaredConstructor(String.class, int.class);

        constructor.setAccessible(true);

        // 강제로 인스턴스 생성 시도
        ConfigManager instance2 = constructor.newInstance("INSTANCE", 0);
    }
}
```

실행 결과는 다음과 같다.

```
Exception in thread "main" java.lang.IllegalArgumentException:
    Cannot reflectively create enum objects
```

예외가 발생하며 생성이 완전히 차단된다. 이것은 개발자가 작성한 방어 코드가 아니라 **JVM 자체가 차단하는 것**이다. `Constructor.newInstance()` 메서드의 실제 소스코드를 보면 비밀이 드러난다.

```java
// java.lang.reflect.Constructor 내부 (JDK 소스코드)
public T newInstance(Object... initargs) throws ... {
    // ...
    if ((clazz.getModifiers() & Modifier.ENUM) != 0)
        throw new IllegalArgumentException("Cannot reflectively create enum objects");
    // ...
}
```

`newInstance()`가 호출되는 시점에 대상 클래스가 Enum인지 먼저 검사하고, Enum이면 무조건 예외를 던진다. 이 검사는 JVM 레벨에 하드코딩되어 있기 때문에 개발자가 우회할 수 있는 방법이 없다. 일반 클래스의 `private` 생성자 방어가 "잠금장치"라면, Enum의 방어는 "애초에 문이 없는 것"에 비유할 수 있다.

---

## 2. 직렬화로 싱글톤 깨뜨리기

직렬화(Serialization)는 객체를 바이트 스트림으로 변환하여 파일에 저장하거나 네트워크로 전송하는 기능이다. 문제는 역직렬화(Deserialization) 과정에서 `ObjectInputStream`이 **생성자를 거치지 않고 새로운 인스턴스를 만든다**는 점이다.

### 2.1 Bill Pugh 방식에 대한 공격

```java
import java.io.Serializable;

public class ConfigManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private ConfigManager() {
        System.out.println("ConfigManager 생성!");
    }

    private static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return Holder.INSTANCE;
    }
}
```

```java
import java.io.*;

public class SerializationAttack {

    public static void main(String[] args) throws Exception {
        ConfigManager instance1 = ConfigManager.getInstance();

        // 1단계: 싱글톤 인스턴스를 파일로 직렬화
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("singleton.ser"))) {
            out.writeObject(instance1);
        }
        System.out.println("직렬화 완료");

        // 2단계: 파일에서 역직렬화 — 이 과정에서 새로운 인스턴스가 생성됨
        ConfigManager instance2;
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("singleton.ser"))) {
            instance2 = (ConfigManager) in.readObject();
        }
        System.out.println("역직렬화 완료");

        // 3단계: 결과 확인
        System.out.println("instance1: " + instance1.hashCode());
        System.out.println("instance2: " + instance2.hashCode());
        System.out.println("같은 객체인가? " + (instance1 == instance2));
    }
}
```

실행 결과는 다음과 같다.

```
ConfigManager 생성!
직렬화 완료
역직렬화 완료
instance1: 1234567890
instance2: 987654321           ← 해시코드가 다름
같은 객체인가? false           ← 싱글톤이 깨졌다
```

눈여겨볼 점은 "ConfigManager 생성!"이 한 번만 출력되었다는 것이다. 역직렬화 과정에서는 생성자가 호출되지 않는다. `ObjectInputStream`이 내부적으로 바이트 스트림에서 직접 객체를 복원하기 때문이다. 따라서 생성자에 어떤 방어 코드를 넣어도 역직렬화 공격은 막을 수 없다.

### 2.2 방어 시도: readResolve() 메서드

Java는 이 문제를 위해 `readResolve()`라는 특별한 메서드를 제공한다. `ObjectInputStream`은 역직렬화 과정의 마지막에 이 메서드가 있는지 확인하고, 있으면 이 메서드가 반환하는 객체를 최종 결과로 사용한다.

```java
public class ConfigManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private ConfigManager() {}

    private static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return Holder.INSTANCE;
    }

    // 역직렬화 시 새 인스턴스 대신 기존 싱글톤 인스턴스를 반환
    private Object readResolve() {
        return Holder.INSTANCE;
    }
}
```

이제 역직렬화를 해도 기존 인스턴스가 반환된다. 하지만 이 방어에도 허점이 있다. 역직렬화 과정에서 새로운 인스턴스가 일단 만들어진 후에 `readResolve()`가 호출되어 교체되는 구조이기 때문에, 아주 짧은 순간이지만 두 번째 인스턴스가 존재한다. 교체되기 전에 내부 필드를 통해 이 인스턴스에 접근하는 정교한 공격이 가능하다. 또한 `readResolve()` 메서드를 추가하는 것 자체를 개발자가 잊을 수 있고, `Serializable`을 구현하는 순간 이 위험이 발생한다는 것을 인지하지 못하는 경우가 많다.

### 2.3 Enum은 왜 직렬화에 안전한가?

```java
public enum ConfigManager {
    INSTANCE;

    private final Map<String, String> configs = new HashMap<>();

    public void setConfig(String key, String value) {
        configs.put(key, value);
    }

    public String getConfig(String key) {
        return configs.get(key);
    }
}
```

```java
public class EnumSerializationTest {

    public static void main(String[] args) throws Exception {
        ConfigManager instance1 = ConfigManager.INSTANCE;
        instance1.setConfig("db.url", "jdbc:mysql://localhost:3306/mydb");

        // 직렬화
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("enum_singleton.ser"))) {
            out.writeObject(instance1);
        }

        // 역직렬화
        ConfigManager instance2;
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("enum_singleton.ser"))) {
            instance2 = (ConfigManager) in.readObject();
        }

        // 결과 확인
        System.out.println("같은 객체인가? " + (instance1 == instance2));
        System.out.println("설정값 유지? " + instance2.getConfig("db.url"));
    }
}
```

실행 결과는 다음과 같다.

```
같은 객체인가? true              ← 동일한 인스턴스
설정값 유지? jdbc:mysql://localhost:3306/mydb
```

Enum이 직렬화에 안전한 이유는 JVM의 직렬화 스펙 자체가 Enum을 특별하게 처리하기 때문이다. 일반 객체는 역직렬화 시 필드 값을 복원하여 새로운 인스턴스를 만들지만, Enum은 다르게 동작한다. 직렬화할 때 Enum의 이름(name)만 저장하고, 역직렬화할 때는 `Enum.valueOf(ConfigManager.class, "INSTANCE")`를 호출하여 기존 Enum 상수를 찾아 반환한다. 새로운 인스턴스를 만드는 과정 자체가 없으므로, `readResolve()` 같은 추가 방어 코드 없이도 원천적으로 안전하다.

---

## 3. 핵심: 누가 방어의 책임을 지는가?

리플렉션 공격에 대해서, 일반 클래스 기반의 모든 방식(기본, synchronized, DCL, Bill Pugh)은 `setAccessible(true)`로 `private` 생성자가 무력화되기 때문에 취약하다. 생성자 내부에 방어 코드를 넣어도 그 방어 코드 자체가 리플렉션으로 우회될 수 있어서 완벽한 방어가 불가능하다. 반면 Enum은 `Constructor.newInstance()` 내부에서 JVM이 Enum 여부를 검사하고 차단하므로, 개발자의 실수와 무관하게 언어 스펙 차원에서 보호된다.

직렬화 공격에 대해서, 일반 클래스가 `Serializable`을 구현하면 역직렬화 시 생성자를 거치지 않고 새 인스턴스가 만들어진다. `readResolve()` 메서드로 방어할 수 있지만, 이 메서드를 추가하는 것을 잊으면 바로 취약해지며, 추가하더라도 일시적으로 두 번째 인스턴스가 존재하는 틈이 있다. Enum은 JVM 직렬화 스펙이 이름만 저장하고 기존 상수를 반환하는 방식이므로, 추가 코드 없이도 안전하다.

| 공격 유형 | 일반 클래스 (Bill Pugh 등) | Enum |
|---|---|---|
| 리플렉션 | `setAccessible(true)`로 생성자 돌파 가능. 방어 코드도 리플렉션으로 우회 가능 | JVM이 `newInstance()` 내부에서 차단. 우회 불가 |
| 직렬화 | 역직렬화 시 새 인스턴스 생성. `readResolve()`로 방어 가능하나 개발자가 잊을 수 있음 | JVM이 이름만 저장/복원. 새 인스턴스 생성 자체가 없음 |
| 방어 책임 | 개발자 | JVM / Java 언어 스펙 |

일반 클래스 방식에서는 개발자가 리플렉션 방어 코드, `readResolve()` 메서드 등을 직접 챙겨야 하고, 하나라도 빠뜨리면 싱글톤이 깨진다. Enum 방식에서는 JVM과 Java 언어 스펙이 방어의 책임을 진다. 개발자가 아무것도 하지 않아도 안전하다. 이것이 Joshua Bloch가 Effective Java에서 "Enum 싱글톤이 가장 좋은 방법"이라고 강조한 이유다.

---

## 4. 그럼에도 Enum 싱글톤의 한계

Enum이 가장 안전한 건 맞지만, 실무에서 항상 최선인 것은 아니다.

Enum은 다른 클래스를 **상속할 수 없다.** Java에서 모든 Enum은 암묵적으로 `java.lang.Enum`을 상속하고 있기 때문에, 다중 상속이 불가능한 Java에서는 다른 클래스를 확장할 수 없다. 인터페이스 구현은 가능하지만, 기존 클래스 계층 구조에 편입시켜야 하는 상황에서는 사용할 수 없다.

Enum은 **Lazy 초기화가 불가능하다.** Enum 상수는 클래스 로딩 시점에 즉시 생성되므로, 생성 비용이 크고 지연 초기화가 필요한 경우에는 Bill Pugh 방식이 더 적합하다.

따라서 실무에서의 선택 기준은 다음과 같다. Spring 환경이라면 GoF 싱글톤을 직접 구현할 필요 없이 컨테이너에 위임하면 된다. 순수 Java 환경에서 직렬화나 리플렉션 방어가 필요하다면 Enum이 최선이고, 상속이 필요하거나 Lazy 초기화가 중요하다면 Bill Pugh 방식에 `readResolve()`를 추가하는 것이 현실적인 선택이다.