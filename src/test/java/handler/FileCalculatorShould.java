package handler;

import calculators.CustomerRankingCalculator;
import calculators.OrderPriceCalculator;
import calculators.ProductCustomerCalculator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class FileCalculatorShould {

    private final static String CUSTOMERS_CSV = "customers.csv";
    private final static String ORDERS_CSV = "orders.csv";
    private final static String PRODUCTS_CSV = "products.csv";
    private final static String RESULT_CSV = "result.csv";

    private final static Path OUT_DIRECTORY = Paths.get(".");

    private static final String TASK3 = "customerRanking";
    private static final String ORDER_PRICES_CSV = "order_prices.csv";
    private static final String PRODUCT_CUSTOMER_CSV = "product_customers.csv";
    private static final String CUSTOMER_RANKING_CSV = "customer_ranking.csv";

    //Delegation
    @Test
    void fileCalculatorDelegatesToOrderPriceCalculatorTheOrderPricesFile() throws IOException {
        //Given
        OrderPriceCalculator orderPriceCalculator = mock(OrderPriceCalculator.class);
        FileCalculator fileCalculator = new FileCalculator(orderPriceCalculator);
        //When
        fileCalculator.calculateOrderPrices();
        //Then
        verify(orderPriceCalculator).calculateOrderPrices();
    }

    @Test
    void fileCalculatorDelegationBringsExpectedOrdersPrice() throws IOException {
        //Given
        File products = getResourceFileOriginal(PRODUCTS_CSV);
        File orders = getResourceFileOriginal(ORDERS_CSV);

        File expectedOrderPrices = getResourceFileOriginal(ORDER_PRICES_CSV);

        OrderPriceCalculator orderPriceCalculator = spy(new OrderPriceCalculator(products, orders, OUT_DIRECTORY));
        FileCalculator fileCalculator = new FileCalculator(orderPriceCalculator, null, null);
        //When
        final File result = fileCalculator.calculateOrderPrices();
        //Then
        verify(orderPriceCalculator).calculateOrderPrices();

        assertThat(contentOf(result))
                .as("Expected order prices are generated")
                .isEqualToIgnoringNewLines(contentOf(expectedOrderPrices));

    }

    @Test
    void fileCalculatorDelegatesToProductCustomerCalculatorTheProductCustomerFile() throws IOException {
        //Given
        ProductCustomerCalculator productCustomerCalculator = mock(ProductCustomerCalculator.class);
        FileCalculator fileCalculator = new FileCalculator(productCustomerCalculator);
        //When
        fileCalculator.calculateProductCustomers();
        //Then
        verify(productCustomerCalculator).calculateProductCustomers();
    }

    @Test
    void fileCalculatorDelegationBringsExpectedProductCustomer() throws IOException {
        //Given
        File orders = getResourceFileOriginal(ORDERS_CSV);

        File expected = getResourceFileOriginal(PRODUCT_CUSTOMER_CSV);

        ProductCustomerCalculator productCustomerCalculator = spy(new ProductCustomerCalculator(orders, OUT_DIRECTORY));
        FileCalculator fileCalculator = new FileCalculator(productCustomerCalculator);
        //When
        final File result = fileCalculator.calculateProductCustomers();
        //Then
        verify(productCustomerCalculator).calculateProductCustomers();

        assertThat(contentOf(result))
                .as("Expected product customers are generated")
                .isEqualToIgnoringNewLines(contentOf(expected));
    }

    @Test
    void fileCalculatorDelegatesToCustomerRankingCalculatortheCustomerRankingFile() throws IOException {
        //Given
        CustomerRankingCalculator customerRankingCalculator = mock(CustomerRankingCalculator.class);
        FileCalculator fileCalculator = new FileCalculator(customerRankingCalculator);
        //When
        fileCalculator.calculateCustomerRanking();
        //Then
        verify(customerRankingCalculator).calculateCustomerRanking();
    }

    @Test
    void fileCalculatorDelegationBringsExpectedCustomerRanking() throws IOException {
        //Given
        File products = getResourceFileOriginal(PRODUCTS_CSV);
        File customers = getResourceFileOriginal(CUSTOMERS_CSV);
        File orders = getResourceFileOriginal(ORDERS_CSV);

        File expected = getResourceFileOriginal(CUSTOMER_RANKING_CSV);

        CustomerRankingCalculator customerRankingCalculator = spy(new CustomerRankingCalculator(customers, products, orders, OUT_DIRECTORY));
        FileCalculator fileCalculator = new FileCalculator(customerRankingCalculator);
        //When
        final File result = fileCalculator.calculateCustomerRanking();
        //Then
        verify(customerRankingCalculator).calculateCustomerRanking();
        assertThat(contentOf(result))
                .as("Expected customer rankings are generated")
                .isEqualToIgnoringNewLines(contentOf(expected));
    }

    private File getResourceFileOriginal(final String fileName) {

        final String pathToFile = "/originals/" + fileName;

        return new File(getClass().getResource(pathToFile).getFile());
    }

}
