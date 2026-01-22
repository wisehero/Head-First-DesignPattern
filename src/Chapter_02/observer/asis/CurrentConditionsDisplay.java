package Chapter_02.observer.asis;

/**
 * 현재 날씨를 화면에 표시하는 디스플레이
 */
public class CurrentConditionsDisplay {

    public void update(float temperature, float humidity, float pressure) {
        System.out.println("=== 현재 날씨 ===");
        System.out.println("온도: " + temperature + "°C");
        System.out.println("습도: " + humidity + "%");
    }
}
