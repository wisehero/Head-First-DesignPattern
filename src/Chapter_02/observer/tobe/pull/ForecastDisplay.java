package Chapter_02.observer.tobe.pull;

/**
 * 기상 예보 디스플레이
 * Pull 방식: 기압만 가져옴
 */
public class ForecastDisplay implements WeatherObserver{

    private float lastPressure = 1013.25f;

    @Override
    public void update(WeatherData weatherData) {
        // 기압만 Pull!
        float pressure = weatherData.getPressure();
        // temperature, humidity는 필요 없으니 안 가져옴

        System.out.println("=== 기상 예보 ===");

        if (pressure > lastPressure) {
            System.out.println("날씨가 좋아질 것으로 예상됩니다!");
        } else if (pressure < lastPressure) {
            System.out.println("비가 올 수 있으니 우산을 챙기세요.");
        } else {
            System.out.println("현재 날씨가 유지될 것으로 예상됩니다.");
        }

        lastPressure = pressure;
    }
}
