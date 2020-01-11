package calculators;

import org.junit.jupiter.api.Test;

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

    //Task2
    @Test
    void whenThereAreNoOrdersYouGetAProductCustomersFileWithNoRecords() throws IOException {
        //Given
        final String testName = "whenThereAreNoOrdersYouGetAProductCustomersFileWithNoRecords";
        final String testMessage = "When there are no orders, it brings a record empty product customers file";
        final ProductCustomerCalculator productCustomerCalculator = buildProductCustomerCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = productCustomerCalculator.calculateProductCustomers();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as(testMessage)
                .isEqualToIgnoringNewLines(contentOf(expected));
    }


    @Test
    void whenThereIsAnOrderWithASingleProductReturnThatProductAssociatedToTheCustomer() throws IOException {
        //Given
        final String testName = "whenThereIsAnOrderWithASingleProductReturnThatProductAssociatedToTheCustomer";
        final String testMessage = "When there is a single order with a single product, " +
                "it brings a record associating the product with the customer of the order";
        final ProductCustomerCalculator productCustomerCalculator = buildProductCustomerCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = productCustomerCalculator.calculateProductCustomers();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as(testMessage)
                .isEqualToIgnoringNewLines(contentOf(expected));
    }

    @Test
    void whenThereIsAnOrderWithMultipleProductReturnThoseProductAssociatedToTheCustomer() throws IOException {
        //Given
        final String testName = "whenThereIsAnOrderWithMultipleProductReturnThoseProductAssociatedToTheCustomer";
        final String testMessage = "When there is a single order with multiple products, " +
                "it brings a record associating all the products with the customer of the order";
        final ProductCustomerCalculator productCustomerCalculator = buildProductCustomerCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = productCustomerCalculator.calculateProductCustomers();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as(testMessage)
                .isEqualToIgnoringNewLines(contentOf(expected));
    }

    @Test
    void whenThereIsMultipleOrdersWithTheSameProductReturnThatProductAssociatedToTheCustomers() throws IOException {
        //Given
        final String testName = "whenThereIsMultipleOrdersWithTheSameProductReturnThatProductAssociatedToTheCustomers";
        final String testMessage = "When there are multiple orders of the same product, " +
                "it brings a record associating all the products with the customer of the order";
        final ProductCustomerCalculator productCustomerCalculator = buildProductCustomerCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = productCustomerCalculator.calculateProductCustomers();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as(testMessage)
                .isEqualToIgnoringNewLines(contentOf(expected));
    }

    @Test
    void whenThereAreMultipleOrdersWithMultipleProductsItBringsTheCorrectAssociations() throws IOException {
        //Given
        final String testName = "whenThereAreMultipleOrdersWithMultipleProductsItBringsTheCorrectAssociations";
        final String testMessage = "When there are multiple orders of multiple products, " +
                "it brings the correct associations";
        final ProductCustomerCalculator productCustomerCalculator = buildProductCustomerCalculator(testName);
        final File expected = getExpected(testName);
        //When
        final File result = productCustomerCalculator.calculateProductCustomers();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
                .as(testMessage)
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
