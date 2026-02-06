package Chapter_05;

public class SynchronizedConfigManager {

    private static SynchronizedConfigManager instance;

    private SynchronizedConfigManager() {
    }

    // Thread Safe 하지만, 매 호출 마다 lock이 걸려 성능 저하가 있을 수 있다.
    private static synchronized SynchronizedConfigManager getInstance() {
        if (instance == null) {
            instance = new SynchronizedConfigManager();
        }

        return instance;
    }
}
