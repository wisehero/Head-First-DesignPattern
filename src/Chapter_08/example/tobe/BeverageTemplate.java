package Chapter_08.example.tobe;

/**
 * 추상 클래스가 "음료 제조"라는 알고리즘의 뼈대를 정의합니다.
 * 이것이 Template Method Pattern의 핵심 구조입니다.
 */
public abstract class BeverageTemplate {

	// 템플릿 메서드: 알고리즘의 골격을 확정
	// final로 선언하여 하위 클래스가 순서를 변경하지 못하게 보호합니다.
	public final void make() {
		boilWater();
		brew();
		pourInCup();
		if (wantsCondiments()) {
			addCondiments();
		}
	}

	// 공통 단계: 모든 음료에 동일한 로직
	// private이 아닌 일반 메서드로, 변경 불필요한 공통 행위입니다.
	private void boilWater() {
		System.out.println("물을 95도로 끓입니다.");
	}

	private void pourInCup() {
		System.out.println("컵에 따릅니다.");
	}

	// 추상 메서드: 하위 클래스에서 반드시 구현해야 하는 "빈칸"
	protected abstract void brew();

	protected abstract void addCondiments();

	// Hook 메서드: 하위 클래스가 "선택적으로" 오버라이드할 수 있는 확장점
	// 기본 구현을 제공하되, 필요한 경우에만 재정의합니다.
	protected boolean wantsCondiments() {
		return true;  // 기본값: 첨가물을 추가한다
	}

}
