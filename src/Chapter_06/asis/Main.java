package Chapter_06.asis;

/**
 * 문제 1 — 가전제품이 추가될 때마다 리모컨을 수정해야 합니다. 선풍기를 추가하고 싶다면? SmartRemote 클래스에 Fan 필드를 추가하고, pressButton6(), pressButton7()을 또 만들어야 합니다.
 * 가전제품이 10개가 되면 리모컨 코드는 걷잡을 수 없이 비대해집니다. 이것은 OCP(개방-폐쇄 원칙)를 정면으로 위반합니다.
 * <p>
 * 문제 2 — 버튼에 기능을 동적으로 할당할 수 없습니다. 현실의 리모컨은 버튼에 원하는 기능을 매핑할 수 있잖아요. 하지만 이 코드에서 pressButton1()은 영원히 "조명 켜기"입니다. 런타임에 "버튼 1을 누르면 에어컨을 켜라"로 바꿀 수 없죠.
 * <p>
 * 문제 3 — Undo(되돌리기)가 불가능합니다. "방금 한 걸 취소해 줘"라는 기능을 넣으려면, 리모컨이 "마지막에 무슨 작업을 했는지"를 기억해야 합니다. 그런데 현재 구조에서는 작업이 메서드 호출로 사라져 버리기 때문에, 추적할 방법이 없습니다.
 * 핵심은 이겁니다. "작업(동사)"이 메서드 호출로만 존재하고, 객체(명사)로 존재하지 않기 때문에 저장, 교체, 취소가 불가능한 것입니다.
 */
public class Main {
    public static void main(String[] args) {
        Light light = new Light();
        AirConditioner aircon = new AirConditioner();
        SmartRemote remote = new SmartRemote(light, aircon);

        remote.pressButton1();  // 조명 켜기
        remote.pressButton3();  // 에어컨 켜기
        remote.pressButton5();  // 온도 낮추기
    }
}
