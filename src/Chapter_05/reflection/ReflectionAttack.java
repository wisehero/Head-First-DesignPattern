package Chapter_05.reflection;

import java.lang.reflect.Constructor;

public class ReflectionAttack {

    public static void main(String[] args) throws Exception{
        // 1. 정상적인 방법으로 싱글톤 인스턴스 획득
        ConfigManagerV2 instance1 = ConfigManagerV2.getInstance();

        // 2. 리플렉션으로 private 생성자에 접근
        Constructor<ConfigManagerV2> constructor = ConfigManagerV2.class.getDeclaredConstructor();

        // private 접근 제어를 무력화
        constructor.setAccessible(true);

        // 3. 리플렉션을 통해 새로운 인스턴스 생성
        ConfigManagerV2 instance2 = constructor.newInstance();

        // 4. 결과 확인
        System.out.println("instance1: " + instance1.hashCode());
        System.out.println("instance2: " + instance2.hashCode());
        System.out.println("같은 객체인가? " + (instance1 == instance2)); // false
    }
}
