package Chapter_05.reflection;

import java.lang.reflect.Constructor;

public class EnumReflectionAttack {
    public static void main(String[] args) throws Exception {
        ConfigManagerV3 instance1 = ConfigManagerV3.INSTANCE;

        // Enum의 생성자는 내부적으로 (String name, int ordinal) 파라미터를 받음
        Constructor<ConfigManagerV3> constructor =
                ConfigManagerV3.class.getDeclaredConstructor(String.class, int.class);

        constructor.setAccessible(true);

        // 강제로 인스턴스 생성 시도
        ConfigManagerV3 instance2 = constructor.newInstance("INSTANCE", 0);
    }
}
