package Chapter_06.asis;

public class Light {

    private boolean on = false;

    public void turnOn() {
        on = true;
        System.out.println("💡 조명이 켜졌습니다.");
    }

    public void turnOff() {
        on = false;
        System.out.println("💡 조명이 꺼졌습니다.");
    }

    public boolean isOn() {
        return on;
    }
}
