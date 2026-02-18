package Chapter_08.example.tobe;

public class Espresso extends BeverageTemplate {

	@Override
	protected void brew() {
		System.out.println("곱게 간 원두로 에스프레소를 추출합니다.");
	}

	@Override
	protected void addCondiments() {
		// Hook에 의해 호출되지 않으므로 빈 구현
	}

	@Override
	protected boolean wantsCondiments() {
		return false;
	}
}
