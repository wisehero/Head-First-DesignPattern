package Chapter_06.asis;

public class SmartRemote {

    private final Light light;
    private final AirConditioner aircon;

    public SmartRemote(Light light, AirConditioner aircon) {
        this.light = light;
        this.aircon = aircon;
    }

    // 버튼 1: 조명 켜기
    public void pressButton1() {
        light.turnOn();
    }

    // 버튼 2: 조명 끄기
    public void pressButton2() {
        light.turnOff();
    }

    // 버튼 3: 에어컨 켜기
    public void pressButton3() {
        aircon.turnOn();
    }

    // 버튼 4: 에어컨 끄기
    public void pressButton4() {
        aircon.turnOff();
    }

    // 버튼 5: 에어컨 온도 낮추기
    public void pressButton5() {
        aircon.setTemperature(aircon.getTemperature() - 1);
    }

}
