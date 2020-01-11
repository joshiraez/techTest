package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Utils {

    public static Long getRecordId(List<String> splittedOrderRecord) {
        return Long.parseLong(splittedOrderRecord.get(0));
    }

    public static BigDecimal getProductPrice(List<String> splittedProductRecord) {
        return new BigDecimal(splittedProductRecord.get(2));
    }

    public static String[] splitProducts(final List<String> splittedOrderRecord) {
        return splittedOrderRecord.get(2).split(" ");
    }

    public static List<String> splitByComma(String line) {
        return asList(line.split(","));
    }

    public static File writeCsv(String header, String fileName, List<List<Object>> contents, Path outDirectory) throws IOException {
        final File file = outDirectory.resolve(fileName).toFile();

        final FileWriter fileWriter = new FileWriter(file);

        fileWriter.append(header).append(System.lineSeparator());

        //Can't use streams because File RecordWriter has checked exceptions and becomes ugly
        for (List<Object> record : contents) {
            fileWriter.append(generateCsvRecord(record)).append(System.lineSeparator());
        }

        fileWriter.close();

        return file;
    }

    private static String generateCsvRecord(List<Object> record) {
        return record.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }
}
