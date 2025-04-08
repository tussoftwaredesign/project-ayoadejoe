package watts;

import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CsvDataReader {
    private final String filePath;
    private final RowFactory factory;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CsvDataReader(String filePath, RowFactory factory) {
        this.filePath = filePath;
        this.factory = factory;
    }

    public Iterator<ElectricalRow> readRows() throws IOException {
        CSVParser parser = CSVParser.parse(new FileReader(filePath), CSVFormat.DEFAULT.withFirstRecordAsHeader());
        Iterator<CSVRecord> csvIterator = parser.iterator();
        return new Iterator<ElectricalRow>() {
            @Override
            public boolean hasNext() {
                return csvIterator.hasNext();
            }

            @Override
            public ElectricalRow next() {
                CSVRecord record = csvIterator.next();
                return factory.createFromCsvRecord(record, TIME_FORMATTER);
            }
        };
    }
}