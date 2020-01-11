import java.io.File;
import java.nio.file.Path;

public class OrderPriceCalculator {
    private final File products;
    private final File orders;

    public OrderPriceCalculator(final File products, final File orders, final Path outDirectory) {
        this.products = products;
        this.orders = orders;
    }

    public File calculateOrderPrices() {
        return null;
    }
}
