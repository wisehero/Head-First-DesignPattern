package Chapter_02.observer.asis;

/**
 * 날씨 통계를 계산하는 모듈
 */
public class StatisticsDisplay {

    private float temperatureSum = 0;
    private int count = 0;

    public void update(float temperature, float humidity, float pressure) {
        temperatureSum += temperature;
        count++;
        float avgTemperature = temperatureSum / count;

        System.out.println("=== 날씨 통계 ===");
        System.out.println("평균 온도: " + avgTemperature + "°C");
        System.out.println("측정 횟수: " + count);
    }
}
