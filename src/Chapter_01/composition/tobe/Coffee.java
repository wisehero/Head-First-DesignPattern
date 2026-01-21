package Chapter_01.composition.tobe;

public interface Coffee {
    String getDescription();

    int getPrice();
}

// 기본 커피 구현체들
class Americano implements Coffee {
    public String getDescription() {
        return "아메리카노";
    }

    public int getPrice() {
        return 4000;
    }
}

class Latte implements Coffee {
    public String getDescription() {
        return "라떼";
    }

    public int getPrice() {
        return 4500;
    }
}

