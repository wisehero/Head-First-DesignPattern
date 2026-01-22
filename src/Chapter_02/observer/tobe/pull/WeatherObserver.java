package Chapter_02.observer.tobe.pull;

/**
 * 옵저버 인터페이스 (Pull 방식)
 *
 * Push와의 차이:
 * - 데이터를 파라미터로 받지 않음
 * - Subject 참조를 받아서 필요한 데이터만 직접 가져감
 */
public interface WeatherObserver {

    // Push: void update(float temperature, float humidity, float pressure);
    // Pull: Subject를 받아서 필요한 것만 가져감
    void update(WeatherData weatherData);
}
