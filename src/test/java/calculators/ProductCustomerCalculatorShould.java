package calculators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

class ProductCustomerCalculatorShould {

    private final static String ORDERS_CSV = "orders.csv";
    private final static String RESULT_CSV = "result.csv";

    private final static Path OUT_DIRECTORY = Paths.get(".");

    private static final String TASK = "productCustomers";

    @ParameterizedTest
    @ValueSource(strings = {
            "whenThereAreNoOrdersYouGetAProductCustomersFileWithNoRecords",
            "whenThereIsAnOrderWithASingleProductReturnThatProductAssociatedToTheCustomer",
            "whenThereIsAnOrderWithMultipleProductReturnThoseProductAssociatedToTheCustomer",
            "whenThereIsMultipleOrdersWithTheSameProductReturnThatProductAssociatedToTheCustomers",
            "whenThereAreMultipleOrdersWithMultipleProductsItBringsTheCorrectAssociations"
    })
    void productCustomerCalculatorBringsExpectedResult(String testCase) throws IOException {
        //Given
        final ProductCustomerCalculator productCustomerCalculator = buildProductCustomerCalculator(testCase);
        final File expected = getExpected(testCase);
        //When
        final File result = productCustomerCalculator.calculateProductCustomers();
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

    private ProductCustomerCalculator buildProductCustomerCalculator(final String testName) {
        return new ProductCustomerCalculator(getOrders(testName), OUT_DIRECTORY);
    }

    private File getOrders(String testName) {
        return getResourceFile(testName, ORDERS_CSV);
    }

    private File getExpected(String testName) {
        return getResourceFile(testName, RESULT_CSV);
    }

}
