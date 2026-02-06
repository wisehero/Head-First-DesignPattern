package Chapter_05;

// Double-Check Locking을 사용한 Thread Safe Singleton 구현
// 여기서는 volatile 키워드를 사용하여 인스턴스가 완전히 초기화되기 전에 다른 스레드가 접근하는 것을 방지
public class DoubleCheckLockConfigManager {

    private static volatile DoubleCheckLockConfigManager instance;

    private DoubleCheckLockConfigManager() {
        // 초기화 코드
    }

    public static DoubleCheckLockConfigManager getInstance() {
        if (instance == null) { // 1차 체크는 락 없이.
            synchronized (DoubleCheckLockConfigManager.class) {
                if (instance == null) {
                    instance = new DoubleCheckLockConfigManager(); // 2차 체크는 락 안에서.
                }
            }
        }
        return instance;
    }
}
