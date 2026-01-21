package Chapter_01.composition.asis;

public abstract class Coffee {
    abstract String getDescription();

    abstract int getPrice();
}

class Americano extends Coffee {
    @Override
    String getDescription() {
        return "Americano";
    }

    @Override
    int getPrice() {
        return 3000;
    }
}

class Latte extends Coffee {
    @Override
    String getDescription() {
        return "Latte";
    }

    @Override
    int getPrice() {
        return 4000;
    }
}

// 옵션을 추가하려면?
class AmericanoWithExtraShot extends Americano {
    String getDescription() { return "아메리카노 + 샷 추가"; }
    int getPrice() { return 4500; }
}

class AmericanoWithSyrup extends Americano {
    String getDescription() { return "아메리카노 + 바닐라 시럽"; }
    int getPrice() { return 4500; }
}

class AmericanoWithExtraShotAndSyrup extends Americano {
    // 샷도 추가하고 시럽도 추가하려면...?
    String getDescription() { return "아메리카노 + 샷 추가 + 바닐라 시럽"; }
    int getPrice() { return 5000; }
}

// 라떼에도 같은 옵션이 필요하다면... 클래스가 폭발적으로 증가