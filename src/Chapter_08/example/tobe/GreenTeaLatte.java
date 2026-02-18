package Chapter_08.example.tobe;

public class GreenTeaLatte extends BeverageTemplate {

	@Override
	protected void brew() {
		System.out.println("녹차를 우려냅니다.");
	}

	@Override
	protected void addCondiments() {
		System.out.println("우유와 설탕을 추가합니다.");
	}

}
