package watts;


import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CsvDataWriter {
    private final String filePath;

    public CsvDataWriter(String filePath) {
        this.filePath = filePath;
    }

    public void writeRows(List<ElectricalRow> rows) throws IOException {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath, true), CSVFormat.DEFAULT.withHeader(
                "ID", "DeviceID", "Time", "Voltage1", "Current1", "Power1", "Energy1", "Frequency1", "PowerFactor1",
                "Voltage2", "Current2", "Power2", "Energy2", "Frequency2", "PowerFactor2", "Voltage3", "Current3",
                "Power3", "Energy3", "Frequency3", "PowerFactor3", "KilowattHr Rate", "VAT", "Cost Data", "IP Address"))) {
            for (ElectricalRow row : rows) {
                printer.printRecord(convertToArray(row));
            }
        }
    }

    private Object[] convertToArray(ElectricalRow row) {
        return new Object[]{
                row.id(), row.deviceId(), row.time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                row.voltage1(), row.current1(), row.power1(), row.energy1(), row.frequency1(), row.powerFactor1(),
                row.voltage2(), row.current2(), row.power2(), row.energy2(), row.frequency2(), row.powerFactor2(),
                row.voltage3(), row.current3(), row.power3(), row.energy3(), row.frequency3(), row.powerFactor3(),
                row.kilowattHrRate(), row.vat(), row.costData(), row.ipAddress()
        };
    }
}