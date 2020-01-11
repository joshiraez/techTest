package main;

import calculators.CustomerRankingCalculator;
import calculators.OrderPriceCalculator;
import calculators.ProductCustomerCalculator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileCalculator {

    private final File customers;
    private final File products;
    private final File orders;
    private final Path outDirectory;
    private CustomerRankingCalculator customerRankingCalculator;
    private ProductCustomerCalculator productCustomerCalculator;
    private OrderPriceCalculator orderPriceCalculator;

    public FileCalculator(final File customers, final File products, final File orders, final Path outDirectory) {

        this.customers = customers;
        this.products = products;
        this.orders = orders;
        this.outDirectory = outDirectory;
    }

    public FileCalculator(final File customers, final File products, final File orders, final Path outDirectory, final OrderPriceCalculator orderPriceCalculator) {

        this.customers = customers;
        this.products = products;
        this.orders = orders;
        this.outDirectory = outDirectory;
        this.orderPriceCalculator = orderPriceCalculator;
    }

    public FileCalculator(final File customers, final File products, final File orders, final Path outDirectory, final ProductCustomerCalculator productCustomerCalculator) {

        this.customers = customers;
        this.products = products;
        this.orders = orders;
        this.outDirectory = outDirectory;
        this.productCustomerCalculator = productCustomerCalculator;
    }

    public FileCalculator(final File customers, final File products, final File orders, final Path outDirectory, final calculators.CustomerRankingCalculator customerRankingCalculator) {

        this.customers = customers;
        this.products = products;
        this.orders = orders;
        this.outDirectory = outDirectory;
        this.customerRankingCalculator = customerRankingCalculator;
    }

    public File calculateOrderPrices() throws IOException {

        return orderPriceCalculator.calculateOrderPrices();
    }

    public File calculateProductCustomers() throws IOException {
        return productCustomerCalculator.calculateProductCustomers();
    }

    public File calculateCustomerRanking() throws IOException {
        return customerRankingCalculator.calculateCustomerRanking();
    }

}
