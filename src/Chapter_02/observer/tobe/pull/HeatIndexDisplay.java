package Chapter_02.observer.tobe.pull;

/**
 * 불쾌지수 디스플레이
 * Pull 방식: 온도와 습도만 가져옴
 */
public class HeatIndexDisplay  implements WeatherObserver{
    @Override
    public void update(WeatherData weatherData) {
        // 온도와 습도만 Pull!
        float temperature = weatherData.getTemperature();
        float humidity = weatherData.getHumidity();
        // pressure는 필요 없으니 안 가져옴

        float heatIndex = calculateHeatIndex(temperature, humidity);

        System.out.println("=== 불쾌지수 ===");
        System.out.println("불쾌지수: " + heatIndex);

        if (heatIndex >= 80) {
            System.out.println("매우 불쾌한 날씨입니다.");
        } else if (heatIndex >= 68) {
            System.out.println("다소 불쾌한 날씨입니다.");
        } else {
            System.out.println("쾌적한 날씨입니다.");
        }
    }

    private float calculateHeatIndex(float t, float rh) {
        return (float) (0.72 * (t + (0.99 * t * rh / 100)) + 40.6);
    }
}
