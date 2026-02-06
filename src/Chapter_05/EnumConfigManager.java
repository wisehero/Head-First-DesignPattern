package Chapter_05;

import java.util.HashMap;
import java.util.Map;

// Enum을 이용한 싱글톤 구현 (Thread Safe)
// JVM이 Enum 인스턴스 생성을 보장하므로, 별도의 동기화 코드가 필요 없음
// 리플렉션 공격에도 안전 why? Enum은 리플렉션으로 생성자 접근이 불가능하기 때문
// 조슈아 블로크의 "Effective Java"에서 권장하는 싱글톤 구현 방법 중 하나
public enum EnumConfigManager {

    INSTANCE;

    private final Map<String, String> configs = new HashMap<>();

    public String getConfig(String key) {
        return configs.get(key);
    }

    public void setConfigs(String key, String value) {
        configs.put(key, value);
    }
}
