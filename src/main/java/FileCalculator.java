import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

    public FileCalculator(final File customers, final File products, final File orders, final Path outDirectory) {

        this.customers = customers;
        this.products = products;
        this.orders = orders;
        this.outDirectory = outDirectory;
    }

    public File calculateOrderPrices() throws IOException {
        final String header = "id,euros";
        final String fileName = "order_prices.csv";

        final Map<Long, BigDecimal> orderPrices = calculateOrderPricesContents();
        final List<List<Object>> contents = transformOrderPricesToRecords(orderPrices);

        return writeCsv(header, fileName, contents);
    }

    public File calculateProductCustomers() throws IOException {
        final String header = "id,customer_ids";
        final String fileName = "product_customers.csv";

        final Map<Long, Set<Long>> customersWhoOrderedProducts = getCustomersWhoOrderedProducts();
        final List<List<Object>> contents = transformCustomersWhoOrderedProductsToRecords(customersWhoOrderedProducts);

        return writeCsv(header, fileName, contents);
    }

    public File calculateCustomerRanking() throws IOException {
        final String header = "id,firstname,lastname,total_euros";
        final String fileName = "customer_ranking.csv";

        final List<List<Object>> contents = Arrays.asList();

        return writeCsv(header, fileName, contents);
    }

    private Map<Long, Set<Long>> getCustomersWhoOrderedProducts() throws IOException {
        final BufferedReader orders = new BufferedReader(new FileReader(this.orders));

        final Map<Long, Set<Long>> customersWhoOrderedProduct = orders.lines()
                .skip(1)
                .map(line -> asList(line.split(",")))
                //The next flat map converts each order record to a stream of pairs <Product, Customer> with all products in the order
                .flatMap(splittedLine ->
                        Stream.of(splittedLine
                                .get(2)
                                .split(" "))
                                .map(
                                        product -> Pair.of(Long.parseLong(product), Long.parseLong(splittedLine.get(1)))
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

    private Map<Long, BigDecimal> calculateOrderPricesContents() throws IOException {

        final Map<Long, Map<Long, Long>> productsOrderedByOrderId = getProductsOrdered();
        final Set<Long> productsToRetrieveInfoFromOrderedProducts =
                getProductsToRetrieveInfoFromOrderedProducts(productsOrderedByOrderId);
        final Map<Long, BigDecimal> productPrices = getProductPrices(productsToRetrieveInfoFromOrderedProducts);

        return getPriceTotalsFromOrders(productsOrderedByOrderId, productPrices);
    }

    private Map<Long, BigDecimal> getPriceTotalsFromOrders(final Map<Long, Map<Long, Long>> productsOrderedByOrderId, final Map<Long, BigDecimal> productPrices) {

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
                .map(line -> asList(line.split(",")))
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

    private Set<Long> getProductsToRetrieveInfoFromOrderedProducts(final Map<Long, Map<Long, Long>> productsOrderedByProductId) {
        return productsOrderedByProductId.values().stream()
                .map(Map::keySet)
                .flatMap(Set::stream)
                .collect(toSet());
    }

    //List of pais orderId to itemsOrdered

    private Map<Long, Map<Long, Long>> getProductsOrdered() throws IOException {
        final BufferedReader orders = new BufferedReader(new FileReader(this.orders));

        final Map<Long, Map<Long, Long>> productsOrderedByOrderId = orders.lines()
                .skip(1)
                .map(line -> asList(line.split(",")))
                .collect(
                        toMap(
                                this::getRecordId,
                                this::countProductsFromOrder
                        )
                );

        orders.close();

        return productsOrderedByOrderId;
    }

    private Map<Long, Long> countProductsFromOrder(List<String> splittedOrderRecord) {
        return Arrays.stream(splittedOrderRecord.get(2).split(" "))
                .map(Long::parseLong)
                .collect(
                        groupingBy(
                                identity(),
                                counting()
                        )
                );
    }

    private Long getRecordId(List<String> splittedOrderRecord) {
        return Long.parseLong(splittedOrderRecord.get(0));
    }

    private File writeCsv(String header, String fileName, List<List<Object>> contents) throws IOException {
        final File file = outDirectory.resolve(fileName).toFile();

        final FileWriter fileWriter = new FileWriter(file);

        fileWriter.append(header).append(System.lineSeparator());

        //Can't use streams because File Writer has checked exceptions and becomes ugly
        for (List<Object> record : contents) {
            fileWriter.append(generateCsvRecord(record)).append(System.lineSeparator());
        }

        fileWriter.close();

        return file;
    }

    private String generateCsvRecord(List<Object> record) {
        return record.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    private List<List<Object>> transformOrderPricesToRecords(final Map<Long, BigDecimal> orderPrices) {
        return orderPrices
                .entrySet()
                .stream()
                .map(
                        order -> asList(
                                //Below cast needed because if not stream casts to a weird interface
                                (Object) order.getKey(),
                                order.getValue()
                        )
                )
                .collect(toList());
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
}
