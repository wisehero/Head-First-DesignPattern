package Chapter_05.reflection;

public class ConfigManager {

    private ConfigManager() {
        System.out.println("ConfigManager instance created.");
    }

    private static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return Holder.INSTANCE;
    }
}
