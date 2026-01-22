package Chapter_02.observer.tobe.push;

/**
 * 불쾌지수 디스플레이 - 새로 추가된 옵저버
 * <p>
 * 핵심: WeatherData 코드를 전혀 수정하지 않고 추가 가능하다.
 */
public class HeatIndexDisplay implements WeatherObserver{
    @Override
    public void update(float temperature, float humidity, float pressure) {
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
        // 불쾌지수 공식 (간략화)
        return (float) (0.72 * (t + (0.99 * t * rh / 100)) + 40.6);
    }
}
