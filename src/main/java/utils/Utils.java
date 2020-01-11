package utils;

import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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

    public static Long getCustomerFromOrderRecord(List<String> splittedLine) {
        return Long.parseLong(splittedLine.get(1));
    }

    public static <T, U extends Comparable<U>> List<Pair<T, U>> sortByValue(final Map<T, U> mapToSortByValue) {
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

    public static <T, U> Map<T, U> zipById(final Map<Long, T> keyMap, final Map<Long, U> valueMap) {
        return keyMap.entrySet()
                .stream()
                .collect(
                        toMap(
                                Map.Entry::getValue,
                                entry -> valueMap.get(entry.getKey())
                        )
                );
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
