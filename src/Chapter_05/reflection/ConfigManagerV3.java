package Chapter_05.reflection;

import java.util.HashMap;
import java.util.Map;

public enum ConfigManagerV3 {

    INSTANCE;

    private final Map<String, String> configs = new HashMap<>();

    public String getConfig(String key) {
        return configs.get(key);
    }

    public void setConfig(String key, String value) {
        configs.put(key, value);
    }

}
