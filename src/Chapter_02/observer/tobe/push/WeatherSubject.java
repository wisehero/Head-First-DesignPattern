package Chapter_02.observer.tobe.push;

/**
 * Subject 인터페이스
 * 옵저버들을 관리하고 알림을 보내는 역할
 */
public interface WeatherSubject {

    void subscribe(WeatherObserver observer);

    void unsubscribe(WeatherObserver observer);

    void notifyObservers();
}
