package Chapter_02.observer.tobe.push;

/**
 * 현재 날씨 디스플레이
 * 온도와 습도만 사용 (기압은 무시)
 */
public class CurrentConditionsDisplay implements WeatherObserver{
    @Override
    public void update(float temperature, float humidity, float pressure) {
        // Push 방식: 필요 없는 pressure도 받지만 사용하지 않음
        System.out.println("=== 현재 날씨 ===");
        System.out.println("온도: " + temperature + "°C");
        System.out.println("습도: " + humidity + "%");
    }
}
