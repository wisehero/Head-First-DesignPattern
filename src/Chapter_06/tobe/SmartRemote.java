package Chapter_06.tobe;

import java.util.Arrays;

/**
 * TO-BE 리모컨: 커맨드 객체만 알고, 가전제품의 존재를 모른다.
 * <p>
 * AS-IS에서는 버튼마다 특정 기능이 하드코딩되어 있었지만,
 * 이제 버튼은 "슬롯"일 뿐이고, 어떤 커맨드든 꽂을 수 있다.
 */
public class SmartRemote {

    private final Command[] slots; // 커맨드 슬롯 배열

    public SmartRemote(int buttonCount) {
        // 모든 슬롯을 "아무것도 안 하는 커맨드"로 초기화
        this.slots = new Command[buttonCount];
        Command noOp = () -> System.out.println("(버튼에 할당된 기능 없음)");
        Arrays.fill(slots, noOp);
    }

    /**
     * 런타임에 버튼에 원하는 커맨드를 할당할 수 있다.
     * AS-IS에서는 불가능했던 "동적 기능 매핑"이 가능해졌다.
     */
    public void setCommand(int slot, Command command) {
        slots[slot] = command;
    }

    /**
     * 리모컨은 그저 슬롯에 들어있는 커맨드의 execute()를 호출할 뿐이다.
     * 그것이 조명인지, 에어컨인지, 선풍기인지 전혀 모르고, 알 필요도 없다.
     */
    public void pressButton(int slot) {
        System.out.println("[버튼 " + slot + " 누름]");
        slots[slot].execute();
    }

}
