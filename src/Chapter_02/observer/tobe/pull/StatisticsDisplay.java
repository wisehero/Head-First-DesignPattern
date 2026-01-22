package Chapter_02.observer.tobe.pull;

/**
 * 날씨 통계 디스플레이
 * Pull 방식: 온도만 가져옴
 */
public class StatisticsDisplay implements WeatherObserver {
    private float temperatureSum = 0;
    private int count = 0;

    @Override
    public void update(WeatherData weatherData) {
        // 온도만 Pull!
        float temperature = weatherData.getTemperature();
        // humidity, pressure는 필요 없으니 안 가져옴

        temperatureSum += temperature;
        count++;
        float avgTemperature = temperatureSum / count;

        System.out.println("=== 날씨 통계 ===");
        System.out.println("평균 온도: " + avgTemperature + "°C");
        System.out.println("측정 횟수: " + count);
    }
}
