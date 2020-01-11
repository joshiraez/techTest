package calculators;

import org.apache.commons.lang3.tuple.Pair;
import utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static utils.Utils.splitProducts;

public class ProductCustomerCalculator {
    private final File orders;
    private final Path outDirectory;

    public ProductCustomerCalculator(final File orders, final Path outDirectory) {

        this.orders = orders;
        this.outDirectory = outDirectory;
    }

    public File calculateProductCustomers() throws IOException {
        final String header = "id,customer_ids";
        final String fileName = "product_customers.csv";

        final Map<Long, Set<Long>> customersWhoOrderedProducts = getCustomersWhoOrderedProducts();
        final List<List<Object>> contents = transformCustomersWhoOrderedProductsToRecords(customersWhoOrderedProducts);

        return Utils.writeCsv(header, fileName, contents, outDirectory);
    }

    private Map<Long, Set<Long>> getCustomersWhoOrderedProducts() throws IOException {
        final BufferedReader orders = new BufferedReader(new FileReader(this.orders));

        final Map<Long, Set<Long>> customersWhoOrderedProduct = orders.lines()
                .skip(1)
                .map(Utils::splitByComma)
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

    private Long getCustomerFromOrderRecord(List<String> splittedLine) {
        return Long.parseLong(splittedLine.get(1));
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
