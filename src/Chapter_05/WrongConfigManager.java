package Chapter_05;

// 잘못된 구현 예시 (Thread Unsafe)
public class WrongConfigManager {

    private static WrongConfigManager instance;

    private WrongConfigManager() {
        // 초기화 코드
    }

    public static WrongConfigManager getInstance() {
        if (instance == null) { // 여러 스레드가 동시에 접근할 경우 문제가 발생할 수 있음
            instance = new WrongConfigManager(); // 인스턴스 2개 이상 생성될 수 있음
        }
        return instance;
    }
}
