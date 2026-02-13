package Chapter_02.observer.tobe.push;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TO-BE: 옵저버 패턴이 적용된 WeatherData
 * <p>
 * 개선점:
 * - 구체적인 디스플레이 클래스를 모름 (Observer 인터페이스만 앎)
 * - 런타임에 동적으로 옵저버 추가/제거 가능
 * - 새로운 디스플레이가 추가되어도 이 클래스는 수정할 필요 없음
 */
public class WeatherData implements WeatherSubject {

    // 구체 클래스가 아닌 인터페이스 타입의 리스트
    private final List<WeatherObserver> observers;

    private float temperature;
    private float humidity;
    private float pressure;

    public WeatherData() {
        this.observers = new ArrayList<>();
    }


    @Override
    public void subscribe(WeatherObserver observer) {
        Objects.requireNonNull(observer, "observer must not be null");
        observers.add(observer);
        System.out.println("[WeatherData] 새로운 옵저버 등록됨: " + observer.getClass().getSimpleName());
    }

    @Override
    public void unsubscribe(WeatherObserver observer) {
        Objects.requireNonNull(observer, "observer must not be null");
        observers.remove(observer);
        System.out.println("[WeatherData] 옵저버 제거됨: " + observer.getClass().getSimpleName());
    }

    @Override
    public void notifyObservers() {
        // 등록된 모든 옵저버에게 알림 (Push: 데이터를 함께 전달)
        // 알림 중 subscribe/unsubscribe가 일어나도 안전하도록 스냅샷 순회
        for (WeatherObserver observer : new ArrayList<>(observers)) {
            observer.update(temperature, humidity, pressure);
        }
    }

    public void setMeasurements(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;

        // 상태 변경 시 옵저버들에게 알림
        notifyObservers();
    }

}
