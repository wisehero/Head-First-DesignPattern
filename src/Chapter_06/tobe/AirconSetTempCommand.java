package Chapter_06.tobe;

import Chapter_06.asis.AirConditioner;

/**
 * 에어컨 온도 변경 커맨드.
 * 실행에 필요한 데이터(targetTemp)도 커맨드 객체 안에 함께 담는다.
 * "22도로 설정해라"라는 요청 전체가 하나의 객체가 된다.
 */
public class AirconSetTempCommand implements Command {

    private final AirConditioner aircon;
    private final int targetTemp;

    public AirconSetTempCommand(AirConditioner aircon, int targetTemp) {
        this.aircon = aircon;
        this.targetTemp = targetTemp;
    }

    @Override
    public void execute() {
        aircon.setTemperature(targetTemp);
    }
}
