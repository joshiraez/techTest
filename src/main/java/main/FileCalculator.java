package main;

import calculators.OrderPriceCalculator;
import calculators.ProductCustomerCalculator;
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
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class FileCalculator {

    private final File customers;
    private final File products;
    private final File orders;
    private final Path outDirectory;
    private ProductCustomerCalculator productCustomerCalculator;
    private OrderPriceCalculator orderPriceCalculator;

    public FileCalculator(final File customers, final File products, final File orders, final Path outDirectory) {

        this.customers = customers;
        this.products = products;
        this.orders = orders;
        this.outDirectory = outDirectory;
    }

    public FileCalculator(final File customers, final File products, final File orders, final Path outDirectory, final OrderPriceCalculator orderPriceCalculator) {

        this.customers = customers;
        this.products = products;
        this.orders = orders;
        this.outDirectory = outDirectory;
        this.orderPriceCalculator = orderPriceCalculator;
    }

    public FileCalculator(final File customers, final File products, final File orders, final Path outDirectory, final ProductCustomerCalculator productCustomerCalculator) {

        this.customers = customers;
        this.products = products;
        this.orders = orders;
        this.outDirectory = outDirectory;
        this.productCustomerCalculator = productCustomerCalculator;
    }

    public File calculateOrderPrices() throws IOException {

        return orderPriceCalculator.calculateOrderPrices();
    }

    public File calculateProductCustomers() throws IOException {

        if (productCustomerCalculator == null) return _calculateProductCustomers();

        return productCustomerCalculator.calculateProductCustomers();
    }

    public File _calculateProductCustomers() throws IOException {
        final String header = "id,customer_ids";
        final String fileName = "product_customers.csv";

        final Map<Long, Set<Long>> customersWhoOrderedProducts = getCustomersWhoOrderedProducts();
        final List<List<Object>> contents = transformCustomersWhoOrderedProductsToRecords(customersWhoOrderedProducts);

        return writeCsv(header, fileName, contents);
    }

    public File calculateCustomerRanking() throws IOException {
        final String header = "id,firstname,lastname,total_euros";
        final String fileName = "customer_ranking.csv";

        final List<Pair<Customer, BigDecimal>> customerExpendings = getCustomersTotalExpendingFromOrders();
        final List<List<Object>> contents = transformCustomerExpendingToRecords(customerExpendings);

        return writeCsv(header, fileName, contents);
    }

    private List<Pair<Customer, BigDecimal>> getCustomersTotalExpendingFromOrders() throws IOException {
        final Map<Long, Map<Long, Long>> productsOrderedByCustomerId = getProductsOrderedByCustomerId();
        final Set<Long> productsToRetrieveInfoFromOrderedProducts =
                getProductsToRetrieveInfoFromOrderedProducts(productsOrderedByCustomerId);
        final Map<Long, BigDecimal> productPrices = getProductPrices(productsToRetrieveInfoFromOrderedProducts);
        final Map<Long, BigDecimal> priceTotalsByCustomerId = getPriceTotals(productsOrderedByCustomerId, productPrices);
        final Map<Long, Customer> customerData = getCustomerData(productsOrderedByCustomerId.keySet());
        final Map<Customer, BigDecimal> customerExpendings = zipById(customerData, priceTotalsByCustomerId);

        return sortByValue(customerExpendings);
    }

    private Map<Long, Customer> getCustomerData(final Set<Long> customerIds) throws IOException {
        final BufferedReader customers = new BufferedReader(new FileReader(this.customers));

        final Map<Long, Customer> customerData = customers.lines()
                .skip(1)
                .map(FileCalculator::splitByComma)
                .filter(splittedLine -> customerIds.contains(getRecordId(splittedLine)))
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

    private Map<Long, Map<Long, Long>> getProductsOrderedByCustomerId() throws IOException {
        final BufferedReader orders = new BufferedReader(new FileReader(this.orders));

        final Map<Long, Map<Long, Long>> productsOrderedByCustomer = orders.lines()
                .skip(1)
                .map(line -> asList(line.split(",")))
                .collect(
                        toMap(
                                this::getCustomerFromOrderRecord,
                                this::countProductsFromOrder,
                                this::addProductCountsTogether
                        )
                );

        orders.close();

        return productsOrderedByCustomer;
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

    private Map<Long, BigDecimal> getProductPrices(final Set<Long> productsToRetrieveInfoFromOrderedProducts) throws IOException {
        final BufferedReader products = new BufferedReader(new FileReader(this.products));

        final Map<Long, BigDecimal> productPrices = products.lines()
                .skip(1)
                .map(FileCalculator::splitByComma)
                .filter(splittedLine ->
                        productsToRetrieveInfoFromOrderedProducts
                                .contains(getRecordId(splittedLine)))
                .collect(
                        toMap(
                                this::getRecordId,
                                this::getProductPrice
                        )
                );

        products.close();

        return productPrices;
    }

    private BigDecimal getProductPrice(List<String> splittedProductRecord) {
        return new BigDecimal(splittedProductRecord.get(2));
    }

    private Set<Long> getProductsToRetrieveInfoFromOrderedProducts(final Map<Long, Map<Long, Long>> productsOrderedById) {
        return productsOrderedById.values().stream()
                .map(Map::keySet)
                .flatMap(Set::stream)
                .collect(toSet());
    }

    private Map<Long, Set<Long>> getCustomersWhoOrderedProducts() throws IOException {
        final BufferedReader orders = new BufferedReader(new FileReader(this.orders));

        final Map<Long, Set<Long>> customersWhoOrderedProduct = orders.lines()
                .skip(1)
                .map(FileCalculator::splitByComma)
                //The next flat map converts each order record to a stream of pairs <Product, model.Customer> with all products in the order
                .flatMap(splittedLine ->
                        Stream.of(splitProducts(splittedLine))
                                .map(
                                        product -> Pair.of(
                                                Long.parseLong(product),
                                                getCustomerFromOrderRecord(splittedLine)
                                        )
                                )
                                .collect(toSet())
                                .stream()
                )
                .collect(
                        toMap(
                                Pair::getKey,
                                pair -> Set.of(pair.getValue()),
                                (alreadyFound, newlyFound) -> {
                                    final Set<Long> alreadyFoundCustomers = new HashSet<>(alreadyFound);
                                    alreadyFoundCustomers.addAll(newlyFound);
                                    return alreadyFoundCustomers;
                                }
                        )
                );

        orders.close();

        return customersWhoOrderedProduct;
    }

    private Map<Long, Long> countProductsFromOrder(List<String> splittedOrderRecord) {
        return Arrays.stream(splitProducts(splittedOrderRecord))
                .map(Long::parseLong)
                .collect(
                        groupingBy(
                                identity(),
                                counting()
                        )
                );
    }

    private String[] splitProducts(final List<String> splittedOrderRecord) {
        return splittedOrderRecord.get(2).split(" ");
    }

    private Long getRecordId(List<String> splittedOrderRecord) {
        return Long.parseLong(splittedOrderRecord.get(0));
    }

    private File writeCsv(String header, String fileName, List<List<Object>> contents) throws IOException {
        return Utils.writeCsv(header, fileName, contents, outDirectory);
    }


    private List<List<Object>> transformCustomersWhoOrderedProductsToRecords(final Map<Long, Set<Long>> customersWhoOrderedProducts) {
        return customersWhoOrderedProducts
                .entrySet()
                .stream()
                .map(
                        productWithCustomers ->
                                asList(
                                        (Object) productWithCustomers.getKey(),
                                        productWithCustomers
                                                .getValue()
                                                .stream()
                                                .map(Object::toString)
                                                .collect(
                                                        joining(" ")
                                                )
                                )
                )
                .collect(toList());
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

    private Long getCustomerFromOrderRecord(List<String> splittedLine) {
        return Long.parseLong(splittedLine.get(1));
    }

    private static List<String> splitByComma(String line) {
        return asList(line.split(","));
    }

    private <T, U extends Comparable<U>> List<Pair<T, U>> sortByValue(final Map<T, U> mapToSortByValue) {
        return mapToSortByValue.entrySet()
                .stream()
                .map(
                        entry -> Pair.of(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .sorted((a, b) -> -a.getValue().compareTo(b.getValue()))
                .collect(toList());
    }

    private <T, U> Map<T, U> zipById(final Map<Long, T> keyMap, final Map<Long, U> valueMap) {
        return keyMap.entrySet()
                .stream()
                .collect(
                        toMap(
                                Map.Entry::getValue,
                                entry -> valueMap.get(entry.getKey())
                        )
                );
    }
}
