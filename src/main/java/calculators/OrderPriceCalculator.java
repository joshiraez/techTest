package calculators;

import utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class OrderPriceCalculator {
    private final File products;
    private final File orders;
    private final Path outDirectory;

    public OrderPriceCalculator(final File products, final File orders, final Path outDirectory) {
        this.products = products;
        this.orders = orders;
        this.outDirectory = outDirectory;
    }

    public File calculateOrderPrices() throws IOException {
        final String header = "id,euros";
        final String fileName = "order_prices.csv";

        final Map<Long, BigDecimal> orderPrices = calculateOrderPricesContents();
        final List<List<Object>> contents = transformOrderPricesToRecords(orderPrices);

        return Utils.writeCsv(header, fileName, contents, outDirectory);
    }

    private Map<Long, BigDecimal> calculateOrderPricesContents() throws IOException {

        final Map<Long, Map<Long, Long>> productsOrderedByOrderId = getProductsOrderedByOrderId();
        final Set<Long> productsToRetrieveInfoFromOrderedProducts =
                getProductsToRetrieveInfoFromOrderedProducts(productsOrderedByOrderId);
        final Map<Long, BigDecimal> productPrices = getProductPrices(productsToRetrieveInfoFromOrderedProducts);

        return getPriceTotals(productsOrderedByOrderId, productPrices);
    }

    //List of orderId to itemsOrdered
    private Map<Long, Map<Long, Long>> getProductsOrderedByOrderId() throws IOException {
        final BufferedReader orders = new BufferedReader(new FileReader(this.orders));

        final Map<Long, Map<Long, Long>> productsOrderedByOrderId = orders.lines()
                .skip(1)
                .map(Utils::splitByComma)
                .collect(
                        toMap(
                                Utils::getRecordId,
                                this::countProductsFromOrder
                        )
                );

        orders.close();

        return productsOrderedByOrderId;
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
                .filter(splittedLine -> productsToRetrieveInfoFromOrderedProducts
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


}
