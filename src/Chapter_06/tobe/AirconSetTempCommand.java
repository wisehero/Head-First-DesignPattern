package Chapter_06.tobe;

import Chapter_06.asis.AirConditioner;

/**
 * 에어컨 온도 변경 커맨드.
 * 실행에 필요한 데이터(targetTemp)도 커맨드 객체 안에 함께 담는다.
 * "22도로 설정해라"라는 요청 전체가 하나의 객체가 된다.
 * <p>
 * 온도 변경 커맨드에서 undo의 핵심:
 * execute() 시점에 "이전 온도"를 백업해 두는 것이다.
 * 이렇게 커맨드 객체가 undo에 필요한 상태를 자기 안에 보관한다.
 */
public class AirconSetTempCommand implements Command {

    private final AirConditioner aircon;
    private final int targetTemp;
    private int previousTemp;  // ← undo를 위한 상태 백업


    public AirconSetTempCommand(AirConditioner aircon, int targetTemp) {
        this.aircon = aircon;
        this.targetTemp = targetTemp;
    }

    @Override
    public void execute() {
        this.previousTemp = aircon.getTemperature();  // 이전 온도 백업
        aircon.setTemperature(targetTemp);
    }

    @Override
    public void undo() {
        aircon.setTemperature(previousTemp);  // 이전 온도로 복원
    }
}
