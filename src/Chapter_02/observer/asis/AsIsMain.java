package Chapter_02.observer.asis;

public class AsIsMain {
    public static void main(String[] args) {
        WeatherData weatherData = new WeatherData();

        // 새로운 측정값이 들어옴
        System.out.println("========== 첫 번째 측정 ==========\n");
        weatherData.setMeasurements(25.0f, 65.0f, 1013.25f);

        System.out.println("\n========== 두 번째 측정 ==========\n");
        weatherData.setMeasurements(27.0f, 70.0f, 1009.50f);

        System.out.println("\n========== 세 번째 측정 ==========\n");
        weatherData.setMeasurements(22.0f, 55.0f, 1015.00f);
    }
}
