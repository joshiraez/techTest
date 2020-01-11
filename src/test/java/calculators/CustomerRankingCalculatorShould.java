package calculators;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

class CustomerRankingCalculatorShould {

    private final static String CUSTOMERS_CSV = "customers.csv";
    private final static String ORDERS_CSV = "orders.csv";
    private final static String PRODUCTS_CSV = "products.csv";
    private final static String RESULT_CSV = "result.csv";

    private static final String TASK = "customerRanking";

    private final static Path OUT_DIRECTORY = Paths.get(".");


    @Test
    void whenThereAreNoOrdersTheCustomerRankingWillHaveNoRecords() throws IOException {
        //Given
        final String testName = "whenThereAreNoOrdersTheCustomerRankingWillHaveNoRecords";
        final String testMessage = "When there are no orders, it brings a record empty customer ranking file";
        final CustomerRankingCalculator customerRankingCalculator = buildCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = customerRankingCalculator.calculateCustomerRanking();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as(testMessage)
                .isEqualToIgnoringNewLines(contentOf(expected));
    }

    @Test
    void whenThereIsASingleOrderCustomerRankingWillBringThatCustomerWithOrderPrice() throws IOException {
        //Given
        final String testName = "whenThereIsASingleOrderCustomerRankingWillBringThatCustomerWithOrderPrice";
        final String testMessage = "When there is a single order, it brings the customer with the order total";
        final CustomerRankingCalculator customerRankingCalculator = buildCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = customerRankingCalculator.calculateCustomerRanking();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as(testMessage)
                .isEqualToIgnoringNewLines(contentOf(expected));
    }

    @Test
    void whenThereAreMultipleOrdersForTheSameCustomerItWillBringTheSumOfTheOrderCostsForTheCustomer() throws IOException {
        //Given
        final String testName = "whenThereAreMultipleOrdersForTheSameCustomerItWillBringTheSumOfTheOrderCostsForTheCustomer";
        final String testMessage = "When there many orders for the same customer, it brings the customer with the total of the orders";
        final CustomerRankingCalculator customerRankingCalculator = buildCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = customerRankingCalculator.calculateCustomerRanking();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as(testMessage)
                .isEqualToIgnoringNewLines(contentOf(expected));
    }

    @Test
    void whenThereAreMultipleOrderFromMultiplePeopleItGetsItsExpendingTotalsRightAndOrdersThemDescending() throws IOException {
        //Given
        final String testName = "whenThereAreMultipleOrderFromMultiplePeopleItGetsItsExpendingTotalsRightAndOrdersThemDescending";
        final String testMessage = "When there orders from different customers, it sets the total for each customer and ranks them in descending expendings";
        final CustomerRankingCalculator customerRankingCalculator = buildCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = customerRankingCalculator.calculateCustomerRanking();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as(testMessage)
                .isEqualToIgnoringNewLines(contentOf(expected));
    }

    //Task
    private File getResourceFile(final String testName, final String fileName) {

        final String pathToFile = "/" + TASK + "/" + testName + "/" + fileName;

        return new File(getClass().getResource(pathToFile).getFile());
    }

    private CustomerRankingCalculator buildCalculator(final String testName) {
        return new CustomerRankingCalculator(getCustomers(testName), getProducts(testName), getOrders(testName), OUT_DIRECTORY);
    }

    private File getCustomers(final String testName) {
        return getResourceFile(testName, CUSTOMERS_CSV);
    }

    private File getProducts(String testName) {
        return getResourceFile(testName, PRODUCTS_CSV);
    }

    private File getOrders(String testName) {
        return getResourceFile(testName, ORDERS_CSV);
    }

    private File getExpected(String testName) {
        return getResourceFile(testName, RESULT_CSV);
    }

}
