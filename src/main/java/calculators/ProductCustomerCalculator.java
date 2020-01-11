package calculators;

import java.io.File;
import java.nio.file.Path;

public class ProductCustomerCalculator {
    private final File orders;
    private final Path outDirectory;

    public ProductCustomerCalculator(final File orders, final Path outDirectory) {

        this.orders = orders;
        this.outDirectory = outDirectory;
    }

    public File calculateProductCustomers() {
        return null;
    }

//    public File _calculateProductCustomers() throws IOException {
//        final String header = "id,customer_ids";
//        final String fileName = "product_customers.csv";
//
//        final Map<Long, Set<Long>> customersWhoOrderedProducts = getCustomersWhoOrderedProducts();
//        final List<List<Object>> contents = transformCustomersWhoOrderedProductsToRecords(customersWhoOrderedProducts);
//
//        return Utils.writeCsv(header, fileName, contents, outDirectory);
//    }
//
//    private Map<Long, Set<Long>> getCustomersWhoOrderedProducts() throws IOException {
//        final BufferedReader orders = new BufferedReader(new FileReader(this.orders));
//
//        final Map<Long, Set<Long>> customersWhoOrderedProduct = orders.lines()
//                .skip(1)
//                .map(FileCalculator::splitByComma)
//                //The next flat map converts each order record to a stream of pairs <Product, model.Customer> with all products in the order
//                .flatMap(splittedLine ->
//                        Stream.of(splitProducts(splittedLine))
//                                .map(
//                                        product -> Pair.of(
//                                                Long.parseLong(product),
//                                                getCustomerFromOrderRecord(splittedLine)
//                                        )
//                                )
//                                .collect(toSet())
//                                .stream()
//                )
//                .collect(
//                        toMap(
//                                Pair::getKey,
//                                pair -> Set.of(pair.getValue()),
//                                (alreadyFound, newlyFound) -> {
//                                    final Set<Long> alreadyFoundCustomers = new HashSet<>(alreadyFound);
//                                    alreadyFoundCustomers.addAll(newlyFound);
//                                    return alreadyFoundCustomers;
//                                }
//                        )
//                );
//
//        orders.close();
//
//        return customersWhoOrderedProduct;
//    }
}
