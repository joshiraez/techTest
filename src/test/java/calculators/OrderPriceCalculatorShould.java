package calculators;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

class OrderPriceCalculatorShould {

    private final static String ORDERS_CSV = "orders.csv";
    private final static String PRODUCTS_CSV = "products.csv";
    private final static String RESULT_CSV = "result.csv";

    private static final String TASK = "orderPrices";

    private final static Path OUT_DIRECTORY = Paths.get(".");

    @Test
    void whenThereAreNoOrdersOrderPricesWillHaveNoOrders() throws IOException {
        //Given
        final String testName = "whenThereAreNoOrdersOrderPricesWillHaveNoOrders";
        final OrderPriceCalculator orderPriceCalculator = buildCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = orderPriceCalculator.calculateOrderPrices();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as("No orders has no orders records on order_prices")
                .isEqualToIgnoringNewLines(contentOf(expected));
    }


    @Test
    void whenThereAreOneOrderWithOneItemGetThePriceOfThatItem() throws IOException {
        //Given
        final String testName = "whenThereAreOneOrderWithOneItemGetThePriceOfThatItem";
        final OrderPriceCalculator orderPriceCalculator = buildCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = orderPriceCalculator.calculateOrderPrices();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as("When only one order with one product, it should bring the price of the product")
                .isEqualToIgnoringNewLines(contentOf(expected));

    }

    @Test
    void whenThereIsOneOrderWithMultipleProductsThePriceShouldBeTheSumOfPrices() throws IOException {
        //Given
        final String testName = "whenThereIsOneOrderWithMultipleProductsThePriceShouldBeTheSumOfPrices";
        final OrderPriceCalculator orderPriceCalculator = buildCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = orderPriceCalculator.calculateOrderPrices();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as("When only one order but with multiple products, it should bring the sum of the prices")
                .isEqualToIgnoringNewLines(contentOf(expected));

    }

    @Test
    void whenThereIsMultipleOrdersYouGetTheSumPriceOfItsProductsForEachOrder() throws IOException {
        //Given
        final String testName = "whenThereIsMultipleOrdersYouGetTheSumPriceOfItsProductsForEachOrder";
        final OrderPriceCalculator orderPriceCalculator = buildCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = orderPriceCalculator.calculateOrderPrices();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as("It calculates order prices correctly with multiple orders")
                .isEqualToIgnoringNewLines(contentOf(expected));

    }

    //Utils
    private File getResourceFile(final String testName, final String fileName) {

        final String pathToFile = "/" + TASK + "/" + testName + "/" + fileName;

        return new File(getClass().getResource(pathToFile).getFile());
    }

    private OrderPriceCalculator buildCalculator(final String testName) {
        return new OrderPriceCalculator(getProducts(testName), getOrders(testName), OUT_DIRECTORY);
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
