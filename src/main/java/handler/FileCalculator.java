package handler;

import calculators.CustomerRankingCalculator;
import calculators.OrderPriceCalculator;
import calculators.ProductCustomerCalculator;

import java.io.File;
import java.io.IOException;

public class FileCalculator {

    private CustomerRankingCalculator customerRankingCalculator;
    private ProductCustomerCalculator productCustomerCalculator;
    private OrderPriceCalculator orderPriceCalculator;

    public FileCalculator(final OrderPriceCalculator orderPriceCalculator, final ProductCustomerCalculator productCustomerCalculator, final CustomerRankingCalculator customerRankingCalculator) {

        this.orderPriceCalculator = orderPriceCalculator;
        this.productCustomerCalculator = productCustomerCalculator;
        this.customerRankingCalculator = customerRankingCalculator;
    }

    public FileCalculator(final OrderPriceCalculator orderPriceCalculator) {

        this.orderPriceCalculator = orderPriceCalculator;
    }

    public FileCalculator(final ProductCustomerCalculator productCustomerCalculator) {

        this.productCustomerCalculator = productCustomerCalculator;
    }

    public FileCalculator(final CustomerRankingCalculator customerRankingCalculator) {

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
