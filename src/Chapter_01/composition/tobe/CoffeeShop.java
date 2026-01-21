package Chapter_01.composition.tobe;

public class CoffeeShop {
    public static void main(String[] args) {
        // 그냥 아메리카노 시키기
        CoffeeOrder order1 = new CoffeeOrder(new Americano());
        System.out.println(order1.getDescription());
        System.out.println(order1.getTotalPrice());

        // 아메리카노에 샷 추가하고 바닐라 시럽 말아버리기
        CoffeeOrder order2 = new CoffeeOrder(new Americano()).addOption(new ExtraShot()).addOption(new VanillaSyrup());
        System.out.println(order2.getDescription());
        System.out.println(order2.getTotalPrice());

        // 라떼에 오트밀크 추가하기
        CoffeeOrder order3 = new CoffeeOrder(new Latte()).addOption(new OatMilk());
        System.out.println(order3.getDescription());
        System.out.println(order3.getTotalPrice());
    }
}
