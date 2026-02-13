package Chapter_05.reflection;

// 그렇다면 생성자 안에서 "이미 인스턴스가 존재하면 예외를 던지는" 방어 코드를 넣으면 어떨까요?
public class ConfigManagerV2 {

    private static boolean isCreated = false; // 이것 자체도 리플렉션으로 조작이 가능하다.

    private ConfigManagerV2() {
        if (isCreated) {
            throw new IllegalStateException(
                    "싱글톤 객체는 이미 생성되었습니다! 리플렉션으로 생성할 수 없습니다."
            );
        }
        isCreated = true;
        System.out.println("ConfigManager 생성!");
    }

    private static class Holder {
        private static final ConfigManagerV2 INSTANCE = new ConfigManagerV2();
    }

    public static ConfigManagerV2 getInstance() {
        return Holder.INSTANCE;
    }
}
