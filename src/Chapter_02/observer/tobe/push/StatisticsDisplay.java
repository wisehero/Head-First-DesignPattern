package Chapter_02.observer.tobe.push;

/**
 * 날씨 통계 디스플레이
 * 온도만 사용 (습도, 기압은 무시)
 */
public class StatisticsDisplay implements WeatherObserver{

    private float temperatureSum = 0;
    private int count = 0;

    @Override
    public void update(float temperature, float humidity, float pressure) {
        // Push 방식: 필요 없는 humidity, pressure도 받지만 사용하지 않음
        temperatureSum += temperature;
        count++;
        float avgTemperature = temperatureSum / count;

        System.out.println("=== 날씨 통계 ===");
        System.out.println("평균 온도: " + avgTemperature + "°C");
        System.out.println("측정 횟수: " + count);
    }
}
