package calculators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @ValueSource(strings = {
            "whenThereAreNoOrdersTheCustomerRankingWillHaveNoRecords",
            "whenThereIsASingleOrderCustomerRankingWillBringThatCustomerWithOrderPrice",
            "whenThereAreMultipleOrdersForTheSameCustomerItWillBringTheSumOfTheOrderCostsForTheCustomer",
            "whenThereAreMultipleOrderFromMultiplePeopleItGetsItsExpendingTotalsRightAndOrdersThemDescending"
    })
    void customerRankingCalculatorBringsExpectedResult(String testCase) throws IOException {
        //Given
        final CustomerRankingCalculator customerRankingCalculator = buildCalculator(testCase);
        final File expected = getExpected(testCase);
        //When
        final File result = customerRankingCalculator.calculateCustomerRanking();
        //Then
        assertThat(result).exists();
        assertThat(contentOf(result))
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
