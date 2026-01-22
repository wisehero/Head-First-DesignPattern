package Chapter_02.observer.asis;

/**
 * 기상 예보를 생성하는 모듈
 */
public class ForecastDisplay {

    private float lastPressure = 1013.25f;

    public void update(float temperature, float humidity, float pressure) {
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