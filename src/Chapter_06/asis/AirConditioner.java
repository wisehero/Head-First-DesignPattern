package Chapter_06.asis;

public class AirConditioner {
    private boolean on = false;
    private int temperature = 24;

    public void turnOn() {
        on = true;
        System.out.println("❄️ 에어컨이 켜졌습니다. 현재 온도: " + temperature + "도");
    }

    public void turnOff() {
        on = false;
        System.out.println("❄️ 에어컨이 꺼졌습니다.");
    }

    public void setTemperature(int temp) {
        int old = this.temperature;
        this.temperature = temp;
        System.out.println("❄️ 온도 변경: " + old + "도 → " + temp + "도");
    }

    public int getTemperature() {
        return temperature;
    }

    public boolean isOn() {
        return on;
    }
}
