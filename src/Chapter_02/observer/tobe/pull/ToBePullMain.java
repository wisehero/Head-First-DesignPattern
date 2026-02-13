package Chapter_02.observer.tobe.pull;

public class ToBePullMain {
    public static void main(String[] args) {
        WeatherData weatherData = new WeatherData();

        // Observer들 구독
        weatherData.subscribe(new CurrentConditionsDisplay());
        weatherData.subscribe(new StatisticsDisplay());
        weatherData.subscribe(new ForecastDisplay());
        weatherData.subscribe(new HeatIndexDisplay());
        weatherData.subscribe(new WindDisplay());

        System.out.println("\n========== 첫 번째 측정 ==========\n");
        weatherData.setMeasurements(25.0f, 65.0f, 1013.25f, 4.5f);

        System.out.println("\n========== 두 번째 측정 ==========\n");
        weatherData.setMeasurements(30.0f, 80.0f, 1009.50f, 12.0f);
    }
}
