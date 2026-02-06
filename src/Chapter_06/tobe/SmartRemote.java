package Chapter_06.tobe;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * TO-BE 리모컨: 커맨드 객체만 알고, 가전제품의 존재를 모른다.
 * <p>
 * AS-IS에서는 버튼마다 특정 기능이 하드코딩되어 있었지만,
 * 이제 버튼은 "슬롯"일 뿐이고, 어떤 커맨드든 꽂을 수 있다.
 */
public class SmartRemote {

    private final Command[] slots; // 커맨드 슬롯 배열
    private final Deque<Command> history = new ArrayDeque<>();  // 실행 이력


    private static final Command NO_OP = new Command() {
        @Override public void execute() {
            System.out.println("(할당된 기능 없음)");
        }
        @Override public void undo() {
            System.out.println("(되돌릴 작업 없음)");
        }
    };

    public SmartRemote(int buttonCount) {
        this.slots = new Command[buttonCount];
        Arrays.fill(slots, NO_OP);
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
        System.out.println("\n[버튼 " + slot + " 누름]");
        Command command = slots[slot];
        command.execute();
        history.push(command);  // 실행한 커맨드를 이력에 저장
    }

    /**
     * Undo 버튼: 가장 마지막에 실행한 커맨드의 undo()를 호출한다.
     * 리모컨은 "그것이 조명이었는지 에어컨이었는지" 전혀 몰라도 된다.
     * 그저 이력 스택에서 꺼내서 undo()를 호출할 뿐이다.
     */
    public void pressUndo() {
        System.out.println("\n[Undo 버튼 누름]");
        if (history.isEmpty()) {
            System.out.println("(되돌릴 작업이 없습니다)");
            return;
        }
        Command lastCommand = history.pop();
        lastCommand.undo();
    }

}
