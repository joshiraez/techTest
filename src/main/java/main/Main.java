package main;

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
        File customers = new File(Main.class.getResource(CUSTOMERS_CSV).getFile());
        File products = new File(Main.class.getResource(PRODUCTS_CSV).getFile());
        File orders = new File(Main.class.getResource(ORDERS_CSV).getFile());

        final FileCalculator calculator = new FileCalculator(customers, products, orders, OUT_DIRECTORY);

        calculator.calculateOrderPrices();
        calculator.calculateCustomerRanking();
        calculator.calculateProductCustomers();
    }
}
