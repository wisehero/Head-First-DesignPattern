package Chapter_02.observer.asis;

/**
 * AS-IS: 모든 디스플레이를 직접 알고 있는 WeatherData
 * <p>
 * 문제점:
 * 1. 새로운 디스플레이가 추가되면 이 클래스를 수정해야 함
 * 2. 디스플레이를 제거하려면 이 클래스를 수정해야 함
 * 3. WeatherData가 모든 디스플레이의 구체 클래스를 알고 있음
 * 4. 런타임에 동적으로 디스플레이를 추가/제거할 수 없음
 */
public class WeatherData {

    // 모든 디스플레이를 직접 필드로 가지고 있음
    private CurrentConditionsDisplay currentDisplay;
    private StatisticsDisplay statisticsDisplay;
    private ForecastDisplay forecastDisplay;

    // 날씨 데이터
    private float temperature;
    private float humidity;
    private float pressure;

    public WeatherData() {
        // 디스플레이들을 직접 생성
        this.currentDisplay = new CurrentConditionsDisplay();
        this.statisticsDisplay = new StatisticsDisplay();
        this.forecastDisplay = new ForecastDisplay();
    }

    /**
     * 센서로부터 새로운 측정값을 받았을 때 호출됨
     */
    public void setMeasurements(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;

        // 상태가 변하면 모든 디스플레이에 직접 알림
        measurementsChanged();
    }

    /**
     * 측정값이 변경되었을 때 모든 디스플레이를 갱신
     * <p>
     * 여기가 문제!
     * - 각 디스플레이를 직접 호출함
     * - 새 디스플레이 추가 시 이 메서드를 수정해야 함
     * - 특정 디스플레이만 알림을 끄고 싶어도 코드 수정 필요
     */
    private void measurementsChanged() {
        currentDisplay.update(temperature, humidity, pressure);
        statisticsDisplay.update(temperature, humidity, pressure);
        forecastDisplay.update(temperature, humidity, pressure);

        // 새로운 디스플레이가 추가되면 여기에 계속 추가해야 함
        // heatIndexDisplay.update(temperature, humidity, pressure);
        // alertDisplay.update(temperature, humidity, pressure);
        // mobileAppDisplay.update(temperature, humidity, pressure);
        // ...
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getPressure() {
        return pressure;
    }

}
