package Chapter_01.composition.tobe;

public interface CoffeeOption {
    String getOptionDescription();
    int getAdditionalPrice();
}

// 구체적인 옵션들
class ExtraShot implements CoffeeOption {
    public String getOptionDescription() { return "샷 추가"; }
    public int getAdditionalPrice() { return 500; }
}

class VanillaSyrup implements CoffeeOption {
    public String getOptionDescription() { return "바닐라 시럽"; }
    public int getAdditionalPrice() { return 500; }
}

class OatMilk implements CoffeeOption {
    public String getOptionDescription() { return "오트밀크 변경"; }
    public int getAdditionalPrice() { return 700; }
}
