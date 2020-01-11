package main;

import calculators.OrderPriceCalculator;
import calculators.ProductCustomerCalculator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    private final static String TASK2 = "productCustomers";
    private static final String TASK3 = "customerRanking";
    private static final String ORDER_PRICES_CSV = "order_prices.csv";
    private static final String PRODUCT_CUSTOMER_CSV = "product_customers.csv";

    //Delegation
    @Test
    void fileCalculatorDelegatesToOrderPriceCalculatorTheOrderPricesFile() throws IOException {
        //Given
        File products = getResourceFileOriginal(PRODUCTS_CSV);
        File customers = getResourceFileOriginal(CUSTOMERS_CSV);
        File orders = getResourceFileOriginal(ORDERS_CSV);

        OrderPriceCalculator orderPriceCalculator = mock(OrderPriceCalculator.class);
        FileCalculator fileCalculator = new FileCalculator(customers, products, orders, OUT_DIRECTORY, orderPriceCalculator);
        //When
        fileCalculator.calculateOrderPrices();
        //Then
        verify(orderPriceCalculator).calculateOrderPrices();
    }

    @Test
    void fileCalculatorDelegationBringsExpectedOrdersPrice() throws IOException {
        //Given
        File products = getResourceFileOriginal(PRODUCTS_CSV);
        File customers = getResourceFileOriginal(CUSTOMERS_CSV);
        File orders = getResourceFileOriginal(ORDERS_CSV);

        File expectedOrderPrices = getResourceFileOriginal(ORDER_PRICES_CSV);

        OrderPriceCalculator orderPriceCalculator = spy(new OrderPriceCalculator(products, orders, OUT_DIRECTORY));
        FileCalculator fileCalculator = new FileCalculator(customers, products, orders, OUT_DIRECTORY, orderPriceCalculator);
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
        File products = getResourceFileOriginal(PRODUCTS_CSV);
        File customers = getResourceFileOriginal(CUSTOMERS_CSV);
        File orders = getResourceFileOriginal(ORDERS_CSV);

        ProductCustomerCalculator productCustomerCalculator = spy(new ProductCustomerCalculator(orders, OUT_DIRECTORY));
        FileCalculator fileCalculator = new FileCalculator(customers, products, orders, OUT_DIRECTORY, productCustomerCalculator);
        //When
        fileCalculator.calculateProductCustomers();
        //Then
        verify(productCustomerCalculator).calculateProductCustomers();
    }

    @Test
    void fileCalculatorDelegationBringsExpectedProductCustomer() throws IOException {
        //Given
        File products = getResourceFileOriginal(PRODUCTS_CSV);
        File customers = getResourceFileOriginal(CUSTOMERS_CSV);
        File orders = getResourceFileOriginal(ORDERS_CSV);

        File expectedOrderPrices = getResourceFileOriginal(PRODUCT_CUSTOMER_CSV);

        ProductCustomerCalculator productCustomerCalculator = spy(new ProductCustomerCalculator(orders, OUT_DIRECTORY));
        FileCalculator fileCalculator = new FileCalculator(customers, products, orders, OUT_DIRECTORY, productCustomerCalculator);
        //When
        final File result = fileCalculator.calculateProductCustomers();
        //Then
        verify(productCustomerCalculator).calculateProductCustomers();

        assertThat(contentOf(result))
                .as("Expected product customers are generated")
                .isEqualToIgnoringNewLines(contentOf(expectedOrderPrices));
    }

    //Task3
    @Test
    void whenThereAreNoOrdersTheCustomerRankingWillHaveNoRecords() throws IOException {
        //Given
        final String testName = "whenThereAreNoOrdersTheCustomerRankingWillHaveNoRecords";
        final String task = TASK3;
        final String testMessage = "When there are no orders, it brings a record empty customer ranking file";
        final FileCalculator fileCalculator = buildFileCalculatorForTest(testName, task);
        final File expected = withTestName.apply(testName, getResult).apply(task);
        //When
        final File result = fileCalculator.calculateCustomerRanking();
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
        final String task = TASK3;
        final String testMessage = "When there is a single order, it brings the customer with the order total";
        final FileCalculator fileCalculator = buildFileCalculatorForTest(testName, task);
        final File expected = withTestName.apply(testName, getResult).apply(task);
        //When
        final File result = fileCalculator.calculateCustomerRanking();
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
        final String task = TASK3;
        final String testMessage = "When there many orders for the same customer, it brings the customer with the total of the orders";
        final FileCalculator fileCalculator = buildFileCalculatorForTest(testName, task);
        final File expected = withTestName.apply(testName, getResult).apply(task);
        //When
        final File result = fileCalculator.calculateCustomerRanking();
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
        final String task = TASK3;
        final String testMessage = "When there orders from different customers, it sets the total for each customer and ranks them in descending expendings";
        final FileCalculator fileCalculator = buildFileCalculatorForTest(testName, task);
        final File expected = withTestName.apply(testName, getResult).apply(task);
        //When
        final File result = fileCalculator.calculateCustomerRanking();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as(testMessage)
                .isEqualToIgnoringNewLines(contentOf(expected));
    }

    //Helper methods and closures to reduce boilerplate to get the files
    private File getResourceFile(final String task, final String testName, final String fileName) {

        final String pathToFile = "/" + task + "/" + testName + "/" + fileName;

        return new File(getClass().getResource(pathToFile).getFile());
    }

    private File getResourceFileOriginal(final String fileName) {

        final String pathToFile = "/originals/" + fileName;

        return new File(getClass().getResource(pathToFile).getFile());
    }

    private BiFunction<String, String, File> getProducts =
            (String task, String url) -> getResourceFile(task, url, PRODUCTS_CSV);
    private BiFunction<String, String, File> getOrders =
            (String task, String url) -> getResourceFile(task, url, ORDERS_CSV);
    private BiFunction<String, String, File> getCustomers =
            (String task, String url) -> getResourceFile(task, url, CUSTOMERS_CSV);
    private BiFunction<String, String, File> getResult =
            (String task, String url) -> getResourceFile(task, url, RESULT_CSV);

    private BiFunction<String, BiFunction<String, String, File>, Function<String, File>> withTestName
            = (String testName, BiFunction<String, String, File> onGetter) ->
            taskName -> onGetter.apply(taskName, testName);

    private FileCalculator buildFileCalculatorForTest(String testName, String taskName) {
        final File customers = withTestName.apply(testName, getCustomers).apply(taskName);
        final File products = withTestName.apply(testName, getProducts).apply(taskName);
        final File orders = withTestName.apply(testName, getOrders).apply(taskName);

        return new FileCalculator(customers, products, orders, OUT_DIRECTORY);
    }

    private FileCalculator buildFileCalculatorForTestWithProductCustomer(String testName, String taskName) {
        final File customers = withTestName.apply(testName, getCustomers).apply(taskName);
        final File products = withTestName.apply(testName, getProducts).apply(taskName);
        final File orders = withTestName.apply(testName, getOrders).apply(taskName);

        final ProductCustomerCalculator productCustomerCalculator = new ProductCustomerCalculator(orders, OUT_DIRECTORY);

        return new FileCalculator(customers, products, orders, OUT_DIRECTORY, productCustomerCalculator);
    }
}
