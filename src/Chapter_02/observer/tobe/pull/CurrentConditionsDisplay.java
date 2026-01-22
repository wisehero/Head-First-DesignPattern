package Chapter_02.observer.tobe.pull;

/**
 * 현재 날씨 디스플레이
 * Pull 방식: 온도와 습도만 가져옴
 */
public class CurrentConditionsDisplay implements WeatherObserver {
    @Override
    public void update(WeatherData weatherData) {
        // 필요한 데이터만 Pull!
        float temperature = weatherData.getTemperature();
        float humidity = weatherData.getHumidity();
        // pressure는 필요 없으니 안 가져옴

        System.out.println("=== 현재 날씨 ===");
        System.out.println("온도: " + temperature + "°C");
        System.out.println("습도: " + humidity + "%");
    }
}
