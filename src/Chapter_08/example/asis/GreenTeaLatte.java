package Chapter_08.example.asis;

public class GreenTeaLatte {

	public void make(){
		boilWater();          // 물 끓이기  ← 동일!
		brewTea();            // 차 우리기
		pourInCup();          // 컵에 따르기 ← 동일!
		addMilkAndSugar();    // 우유/설탕 추가
	}

	private void boilWater() {
		System.out.println("물을 100도로 끓입니다.");  // 중복!
	}

	private void brewTea() {
		System.out.println("녹차를 우려냅니다.");
	}

	private void pourInCup() {
		System.out.println("컵에 따릅니다.");  // 중복!
	}

	private void addMilkAndSugar() {
		System.out.println("우유와 설탕을 추가합니다.");
	}
}
