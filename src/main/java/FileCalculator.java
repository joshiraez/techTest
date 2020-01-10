import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

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

        final File file = outDirectory.resolve(fileName).toFile();

        final FileWriter fileWriter = new FileWriter(file);

        fileWriter.append(header + "\n");

        fileWriter.close();

        return file;
    }
}
