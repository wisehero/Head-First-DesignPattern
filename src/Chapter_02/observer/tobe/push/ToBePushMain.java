package Chapter_02.observer.tobe.push;

public class ToBePushMain {
    public static void main(String[] args) {
        // 1. Subject 생성
        WeatherData weatherData = new WeatherData();

        // 2. Observer들 생성
        CurrentConditionsDisplay currentDisplay = new CurrentConditionsDisplay();
        StatisticsDisplay statisticsDisplay = new StatisticsDisplay();
        ForecastDisplay forecastDisplay = new ForecastDisplay();

        // 3. Observer들이 자발적으로 구독 (AS-IS와 가장 큰 차이!)
        weatherData.subscribe(currentDisplay);
        weatherData.subscribe(statisticsDisplay);
        weatherData.subscribe(forecastDisplay);

        // 4. 측정값 변경 → 자동으로 모든 옵저버에게 알림
        System.out.println("\n========== 첫 번째 측정 ==========\n");
        weatherData.setMeasurements(25.0f, 65.0f, 1013.25f);

        System.out.println("\n========== 두 번째 측정 ==========\n");
        weatherData.setMeasurements(27.0f, 70.0f, 1009.50f);

        // 5. 런타임에 옵저버 제거 가능! (AS-IS에서는 불가능했음)
        System.out.println("\n========== 통계 디스플레이 구독 취소 ==========\n");
        weatherData.unsubscribe(statisticsDisplay);

        System.out.println("\n========== 세 번째 측정 ==========\n");
        weatherData.setMeasurements(22.0f, 55.0f, 1015.00f);

        // 6. 런타임에 새로운 옵저버 추가 가능!
        System.out.println("\n========== 불쾌지수 디스플레이 추가 ==========\n");
        weatherData.subscribe(new HeatIndexDisplay());

        System.out.println("\n========== 네 번째 측정 ==========\n");
        weatherData.setMeasurements(30.0f, 80.0f, 1008.00f);
    }
}
