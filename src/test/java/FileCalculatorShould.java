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
                .isEqualToNormalizingNewlines(contentOf(expected));
    }

    //Helper methods and closures to reduce boilerplate to get the files
    private File getResource(final String task, final String testName, final String fileName) {

        final String pathToFile = task + "/" + testName + "/" + fileName;

        return new File(getClass().getResource(pathToFile).getFile());
    }

    private BiFunction<String, String, File> getProducts =
            (String task, String url) -> getResource(task, url, PRODUCTS_CSV);
    private BiFunction<String, String, File> getOrders =
            (String task, String url) -> getResource(task, url, ORDERS_CSV);
    private BiFunction<String, String, File> getCustomers =
            (String task, String url) -> getResource(task, url, CUSTOMERS_CSV);
    private BiFunction<String, String, File> getResult =
            (String task, String url) -> getResource(task, url, RESULT_CSV);

    private BiFunction<String, BiFunction<String, String, File>, Function<String, File>> withTestName
            = (String testName, BiFunction<String, String, File> onGetter) ->
            taskName -> onGetter.apply(taskName, testName);

    private FileCalculator buildFileCalculatorForTest(String testName, String taskName) {
        final File customers = withTestName.apply(testName, getCustomers).apply(TASK1);
        final File products = withTestName.apply(testName, getProducts).apply(TASK1);
        final File orders = withTestName.apply(testName, getOrders).apply(TASK1);

        return new FileCalculator(customers, products, orders, OUT_DIRECTORY);
    }
}
