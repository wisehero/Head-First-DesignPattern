package Chapter_08.example.tobe;

public class Americano extends BeverageTemplate {

	@Override
	protected void brew() {
		System.out.println("에스프레소를 추출합니다.");
	}

	@Override
	protected void addCondiments() {
		System.out.println("설탕과 크림을 추가합니다.");
	}
}
