package Chapter_05;

// 싱글톤을 만드는 가장 정석적인 방법
// 왜 이게 최선인가?
// - 지연 초기화 (Lazy Initialization) 지원 -> 필요할 때까지 인스턴스 생성을 미룸
// - 스레드 안전 (Thread Safety) -> 클래스 로딩 시점에 JVM이 동기화를 보장
// - 간결한 코드 -> 불필요한 동기화 코드가 없음
public class ConfigManager {

    private ConfigManager() {

    }

    private static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return Holder.INSTANCE;
    }
}
