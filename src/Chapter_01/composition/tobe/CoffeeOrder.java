package Chapter_01.composition.tobe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CoffeeOrder {
    private final Coffee baseCoffee;
    private final List<CoffeeOption> options;

    public CoffeeOrder(Coffee baseCoffee) {
        this.baseCoffee = baseCoffee;
        this.options = new ArrayList<>();
    }

    public CoffeeOrder addOption(CoffeeOption option) {
        options.add(option);
        return this;
    }

    public String getDescription() {
        if (options.isEmpty()) {
            return baseCoffee.getDescription();
        }

        String optionDesc = options.stream()
                .map(CoffeeOption::getOptionDescription)
                .collect(Collectors.joining(", "));

        return baseCoffee.getDescription() + " (" + optionDesc + ")";
    }

    public int getTotalPrice() {
        int optionPrice = options.stream()
                .mapToInt(CoffeeOption::getAdditionalPrice)
                .sum();

        return baseCoffee.getPrice() + optionPrice;
    }
}
