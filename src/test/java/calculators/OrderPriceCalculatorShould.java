package calculators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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


    @ParameterizedTest
    @ValueSource(strings = {
            "whenThereAreNoOrdersOrderPricesWillHaveNoOrders",
            "whenThereAreOneOrderWithOneItemGetThePriceOfThatItem",
            "whenThereIsOneOrderWithMultipleProductsThePriceShouldBeTheSumOfPrices",
            "whenThereIsMultipleOrdersYouGetTheSumPriceOfItsProductsForEachOrder"
    })
    void priceOrderIsGeneratedCorrectly(String testCase) throws IOException {
        //Given
        final OrderPriceCalculator orderPriceCalculator = buildCalculator(testCase);
        final File expected = getExpected(testCase);
        //When
        final File result = orderPriceCalculator.calculateOrderPrices();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
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
