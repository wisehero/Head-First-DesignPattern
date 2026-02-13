package Chapter_02.observer.tobe.pull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TO-BE (Pull 방식): WeatherData
 *
 * Push와의 차이:
 * - notifyObservers()에서 데이터를 전달하지 않고 자기 자신(this)을 전달
 * - Observer가 필요한 데이터를 getter로 가져갈 수 있도록 getter 제공
 */
public class WeatherData {

    private final List<WeatherObserver> observers;

    private float temperature;
    private float humidity;
    private float pressure;
    private float windSpeed;  // 새로 추가!

    public WeatherData() {
        this.observers = new ArrayList<>();
    }

    // === 구독 관리 ===

    public void subscribe(WeatherObserver observer) {
        Objects.requireNonNull(observer, "observer must not be null");
        observers.add(observer);
        System.out.println("[WeatherData] 새로운 옵저버 등록됨: " + observer.getClass().getSimpleName());
    }

    public void unsubscribe(WeatherObserver observer) {
        Objects.requireNonNull(observer, "observer must not be null");
        observers.remove(observer);
        System.out.println("[WeatherData] 옵저버 제거됨: " + observer.getClass().getSimpleName());
    }

    /**
     * Pull 방식의 핵심!
     * 데이터를 보내지 않고, 자기 자신(this)을 전달함
     * Observer가 필요한 데이터를 알아서 가져감
     */
    public void notifyObservers() {
        // 알림 중 subscribe/unsubscribe가 일어나도 안전하도록 스냅샷 순회
        for (WeatherObserver observer : new ArrayList<>(observers)) {
            observer.update(this);
        }
    }

    // === 비즈니스 메서드 ===

    public void setMeasurements(float temperature, float humidity, float pressure) {
        setMeasurements(temperature, humidity, pressure, this.windSpeed);
    }

    public void setMeasurements(float temperature, float humidity, float pressure, float windSpeed) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;

        notifyObservers();
    }

    // === Getter들 (Pull 방식에서 필수!) ===

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getPressure() {
        return pressure;
    }

    public float getWindSpeed() {
        return windSpeed;
    }
}
