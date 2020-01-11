import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

class FileCalculatorShould {

    private final static String CUSTOMERS_CSV = "customers.csv";
    private final static String ORDERS_CSV = "orders.csv";
    private final static String PRODUCTS_CSV = "products.csv";
    private final static String RESULT_CSV = "result.csv";

    private final static Path OUT_DIRECTORY = Paths.get(".");

    private final static String TASK1 = "orderPrices";

    //Task 1. Those can be parameterized
    @Test
    void whenThereAreNoOrdersOrderPricesWillHaveNoOrders() throws IOException {
        //Given
        final String testName = "whenThereAreNoOrdersOrderPricesWillHaveNoOrders";
        final FileCalculator fileCalculator = buildFileCalculatorForTest(testName, TASK1);
        final File expected = withTestName.apply(testName, getResult).apply(TASK1);
        //When
        final File result = fileCalculator.calculateOrderPrices();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as("No orders has no orders records on order_prices")
                .isEqualTo(contentOf(expected));
    }

    @Test
    void whenThereAreOneOrderWithOneItemGetThePriceOfThatItem() throws IOException {
        //Given
        final String testName = "whenThereAreOneOrderWithOneItemGetThePriceOfThatItem";
        final FileCalculator fileCalculator = buildFileCalculatorForTest(testName, TASK1);
        final File expected = withTestName.apply(testName, getResult).apply(TASK1);
        //When
        final File result = fileCalculator.calculateOrderPrices();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as("When only one order with one product, it should bring the price of the product")
                .isEqualTo(contentOf(expected));

    }

    @Test
    void whenThereIsOneOrderWithMultipleProductsThePriceShouldBeTheSumOfPrices() throws IOException {
        //Given
        final String testName = "whenThereIsOneOrderWithMultipleProductsThePriceShouldBeTheSumOfPrices";
        final FileCalculator fileCalculator = buildFileCalculatorForTest(testName, TASK1);
        final File expected = withTestName.apply(testName, getResult).apply(TASK1);
        //When
        final File result = fileCalculator.calculateOrderPrices();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as("When only one order with one product, it should bring the price of the product")
                .isEqualTo(contentOf(expected));

    }

    @Test
    void whenThereIsMultipleOrdersYouGetTheSumPriceOfItsProductsForEachOrder() throws IOException {
        //Given
        final String testName = "whenThereIsMultipleOrdersYouGetTheSumPriceOfItsProductsForEachOrder";
        final FileCalculator fileCalculator = buildFileCalculatorForTest(testName, TASK1);
        final File expected = withTestName.apply(testName, getResult).apply(TASK1);
        //When
        final File result = fileCalculator.calculateOrderPrices();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as("When only one order with one product, it should bring the price of the product")
                .isEqualTo(contentOf(expected));

    }

    //Helper methods and closures to reduce boilerplate to get the files
    private File getResourceFile(final String task, final String testName, final String fileName) {

        final String pathToFile = task + "/" + testName + "/" + fileName;

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
}
