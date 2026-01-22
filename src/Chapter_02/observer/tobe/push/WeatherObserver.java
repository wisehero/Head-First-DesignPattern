package Chapter_02.observer.tobe.push;

/**
 * 옵저버 인터페이스
 * 날씨 데이터 변경을 통보받고 싶은 객체는 이 인터페이스를 구현한다
 * <p>
 * Push 방식: 데이터를 파라미터로 직접 전달받음
 */
public interface WeatherObserver {
    void update(float temperature, float humidity, float pressure);
}
