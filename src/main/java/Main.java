import calculators.CustomerRankingCalculator;
import calculators.OrderPriceCalculator;
import calculators.ProductCustomerCalculator;
import handler.FileCalculator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private final static String CUSTOMERS_CSV = "customers.csv";
    private final static String ORDERS_CSV = "orders.csv";
    private final static String PRODUCTS_CSV = "products.csv";

    private final static Path OUT_DIRECTORY = Paths.get(".");


    public static void main(String[] args) throws IOException {
        final File customers = new File(Main.class.getResource(CUSTOMERS_CSV).getFile());
        final File products = new File(Main.class.getResource(PRODUCTS_CSV).getFile());
        final File orders = new File(Main.class.getResource(ORDERS_CSV).getFile());

        final CustomerRankingCalculator customerRankingCalculator
                = new CustomerRankingCalculator(customers, products, orders, OUT_DIRECTORY);
        final OrderPriceCalculator orderPriceCalculator
                = new OrderPriceCalculator(products, orders, OUT_DIRECTORY);
        final ProductCustomerCalculator productCustomerCalculator
                = new ProductCustomerCalculator(orders, OUT_DIRECTORY);

        final FileCalculator calculator
                = new FileCalculator(
                orderPriceCalculator,
                productCustomerCalculator,
                customerRankingCalculator
        );

        calculator.calculateOrderPrices();
        calculator.calculateCustomerRanking();
        calculator.calculateProductCustomers();
    }
}
