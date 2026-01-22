package Chapter_02.observer.tobe.pull;

/**
 * 풍속이 필요한 새로운 디스플레이
 *
 * 핵심: 기존 Observer들(CurrentConditions, Statistics, Forecast)은
 * 전혀 수정하지 않아도 됨!
 * 풍속이 필요한 이 디스플레이만 getWindSpeed()를 호출하면 됨
 */
public class WindDisplay implements WeatherObserver {
    @Override
    public void update(WeatherData weatherData) {
        // 풍속만 Pull!
        float windSpeed = weatherData.getWindSpeed();

        System.out.println("=== 바람 정보 ===");
        System.out.println("풍속: " + windSpeed + " m/s");

        if (windSpeed >= 14) {
            System.out.println("강풍 주의! 외출 시 주의하세요.");
        } else if (windSpeed >= 9) {
            System.out.println("바람이 다소 강합니다.");
        } else {
            System.out.println("바람이 약합니다.");
        }
    }
}
