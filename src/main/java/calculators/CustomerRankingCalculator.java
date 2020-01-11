package calculators;

import model.Customer;
import org.apache.commons.lang3.tuple.Pair;
import utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class CustomerRankingCalculator {
    private final File customers;
    private final File products;
    private final File orders;
    private final Path outDirectory;

    public CustomerRankingCalculator(final File customers, final File products, final File orders, final Path outDirectory) {

        this.customers = customers;
        this.products = products;
        this.orders = orders;
        this.outDirectory = outDirectory;
    }

    public File calculateCustomerRanking() throws IOException {
        final String header = "id,firstname,lastname,total_euros";
        final String fileName = "customer_ranking.csv";

        final List<Pair<Customer, BigDecimal>> customerExpendings = getCustomersTotalExpendingFromOrders();
        final List<List<Object>> contents = transformCustomerExpendingToRecords(customerExpendings);

        return Utils.writeCsv(header, fileName, contents, outDirectory);
    }

    private List<Pair<Customer, BigDecimal>> getCustomersTotalExpendingFromOrders() throws IOException {
        final Map<Long, Map<Long, Long>> productsOrderedByCustomerId = getProductsOrderedByCustomerId();
        final Set<Long> productsToRetrieveInfoFromOrderedProducts =
                getProductsToRetrieveInfoFromOrderedProducts(productsOrderedByCustomerId);
        final Map<Long, BigDecimal> productPrices = getProductPrices(productsToRetrieveInfoFromOrderedProducts);
        final Map<Long, BigDecimal> priceTotalsByCustomerId = getPriceTotals(productsOrderedByCustomerId, productPrices);
        final Map<Long, Customer> customerData = getCustomerData(productsOrderedByCustomerId.keySet());
        final Map<Customer, BigDecimal> customerExpendings = Utils.zipById(customerData, priceTotalsByCustomerId);

        return Utils.sortByValue(customerExpendings);
    }

    private Map<Long, Map<Long, Long>> getProductsOrderedByCustomerId() throws IOException {
        final BufferedReader orders = new BufferedReader(new FileReader(this.orders));

        final Map<Long, Map<Long, Long>> productsOrderedByCustomer = orders.lines()
                .skip(1)
                .map(line -> asList(line.split(",")))
                .collect(
                        toMap(
                                Utils::getCustomerFromOrderRecord,
                                this::countProductsFromOrder,
                                this::addProductCountsTogether
                        )
                );

        orders.close();

        return productsOrderedByCustomer;
    }

    private Map<Long, Long> countProductsFromOrder(List<String> splittedOrderRecord) {
        return Arrays.stream(Utils.splitProducts(splittedOrderRecord))
                .map(Long::parseLong)
                .collect(
                        groupingBy(
                                identity(),
                                counting()
                        )
                );
    }

    private Map<Long, Long> addProductCountsTogether(Map<Long, Long> oneProductCount, Map<Long, Long> otherProductCount) {
        final HashSet<Long> productsCounted = new HashSet<>(oneProductCount.keySet());
        productsCounted.addAll(otherProductCount.keySet());

        return productsCounted
                .stream()
                .collect(
                        toMap(
                                Function.identity(),
                                product -> Long.sum(
                                        oneProductCount.getOrDefault(product, 0L),
                                        otherProductCount.getOrDefault(product, 0L)
                                )
                        )
                );
    }

    private Set<Long> getProductsToRetrieveInfoFromOrderedProducts(final Map<Long, Map<Long, Long>> productsOrderedById) {
        return productsOrderedById.values().stream()
                .map(Map::keySet)
                .flatMap(Set::stream)
                .collect(toSet());
    }

    private Map<Long, BigDecimal> getProductPrices(final Set<Long> productsToRetrieveInfoFromOrderedProducts) throws IOException {
        final BufferedReader products = new BufferedReader(new FileReader(this.products));

        final Map<Long, BigDecimal> productPrices = products.lines()
                .skip(1)
                .map(Utils::splitByComma)
                .filter(splittedLine ->
                        productsToRetrieveInfoFromOrderedProducts
                                .contains(Utils.getRecordId(splittedLine)))
                .collect(
                        toMap(
                                Utils::getRecordId,
                                Utils::getProductPrice
                        )
                );

        products.close();

        return productPrices;
    }

    private Map<Long, BigDecimal> getPriceTotals(final Map<Long, Map<Long, Long>> productsOrderedByOrderId, final Map<Long, BigDecimal> productPrices) {

        return productsOrderedByOrderId
                .entrySet()
                .stream()
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                order -> calculateOrderTotal(order.getValue(), productPrices)
                        )
                );
    }

    private BigDecimal calculateOrderTotal(final Map<Long, Long> orderedProducts, final Map<Long, BigDecimal> productPrices) {
        return orderedProducts
                .entrySet()
                .stream()
                .map(
                        orderedProduct ->
                                productPrices.get(orderedProduct.getKey()).multiply(BigDecimal.valueOf(orderedProduct.getValue()))
                )
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private Map<Long, Customer> getCustomerData(final Set<Long> customerIds) throws IOException {
        final BufferedReader customers = new BufferedReader(new FileReader(this.customers));

        final Map<Long, Customer> customerData = customers.lines()
                .skip(1)
                .map(Utils::splitByComma)
                .filter(splittedLine -> customerIds.contains(Utils.getRecordId(splittedLine)))
                .map(splittedLine -> new Customer(
                                Long.parseLong(splittedLine.get(0)),
                                splittedLine.get(1),
                                splittedLine.get(2)
                        )
                )
                .collect(
                        toMap(
                                Customer::getId,
                                Function.identity()
                        )
                );

        customers.close();

        return customerData;
    }

    private List<List<Object>> transformCustomerExpendingToRecords(final List<Pair<Customer, BigDecimal>> customerExpendings) {
        return customerExpendings
                .stream()
                .map(
                        expendings -> asList(
                                (Object) expendings.getKey().getId(),
                                expendings.getKey().getFirstName(),
                                expendings.getKey().getLastName(),
                                expendings.getValue()
                        )
                )
                .collect(toList());
    }
}
