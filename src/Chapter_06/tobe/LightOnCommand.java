package Chapter_06.tobe;

import Chapter_06.asis.Light;

/**
 * "조명을 켠다"는 작업을 객체로 캡슐화했다.
 * 이 객체는 누가 실행하든(리모컨이든, 타이머든, 음성비서든) 동일하게 동작한다.
 */
public class LightOnCommand implements Command {

    private final Light light;  // Receiver: 실제로 일을 하는 객체

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.turnOn();  // Receiver에게 위임
    }

    @Override
    public void undo() {
        light.turnOff();
    }
}
