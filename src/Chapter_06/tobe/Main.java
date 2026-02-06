package Chapter_06.tobe;

import Chapter_06.asis.AirConditioner;
import Chapter_06.asis.Light;

public class Main {
    public static void main(String[] args) {
        // --- Receiver 생성 ---
        Light light = new Light();
        AirConditioner aircon = new AirConditioner();

        // --- Command 생성: "작업"을 객체로 만든다 ---
        Command lightOn = new LightOnCommand(light);
        Command lightOff = new LightOffCommand(light);
        Command airconOn = new AirconOnCommand(aircon);
        Command coolDown = new AirconSetTempCommand(aircon, 18);

        // --- Invoker에 커맨드를 할당 ---
        SmartRemote remote = new SmartRemote(4);
        remote.setCommand(0, lightOn);
        remote.setCommand(1, lightOff);
        remote.setCommand(2, airconOn);
        remote.setCommand(3, coolDown);

        // --- 실행 ---
        remote.pressButton(0);  // 💡 조명이 켜졌습니다.
        remote.pressButton(2);  // ❄️ 에어컨이 켜졌습니다. 현재 온도: 24도
        remote.pressButton(3);  // ❄️ 온도 변경: 24도 → 18도

        System.out.println("\n--- 버튼 재할당 ---");
        // 런타임에 버튼 0의 기능을 바꿀 수 있다!
        remote.setCommand(0, coolDown);
        remote.pressButton(0);  // ❄️ 온도 변경: 18도 → 18도 (이미 18도)
    }
}
