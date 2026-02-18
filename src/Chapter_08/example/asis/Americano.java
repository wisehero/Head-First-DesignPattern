package Chapter_08.example.asis;

public class Americano {

	public void make() {
		boilWater(); // 물을 끓이고
		brewCoffee(); // 커피 추출하고
		pourInCup(); // 컵에 따르고,
		addCondiments(); // 설탕과 크림을 추가한다.
	}

	private void boilWater() {
		System.out.println("물을 100도로 끓입니다.");
	}

	private void brewCoffee() {
		System.out.println("에스프레소를 추출합니다.");
	}

	private void pourInCup() {
		System.out.println("컵에 따릅니다.");
	}

	private void addCondiments() {
		System.out.println("설탕과 크림을 추가합니다.");
	}
}
