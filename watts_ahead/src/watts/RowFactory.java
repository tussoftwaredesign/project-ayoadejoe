package watts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.csv.CSVRecord;

//Stateless and reusable across the application, better to be singleton to avoid redundant object creation.
//Factory-ed so that we can centralize row creation logic, handle default values, and support dependency injection or configuration changes 

public class RowFactory {
    private static final RowFactory INSTANCE = new RowFactory();

    private RowFactory() {}

    public static RowFactory getInstance() {
        return INSTANCE;
    }

    public ElectricalRow createDowntimeRow(int id, LocalDateTime time, ElectricalRow previous) {
        return new ElectricalRow(id, previous.deviceId(), time, 0, 0, 0, previous.energy1(), 0, 0,
                0, 0, 0, previous.energy2(), 0, 0, 0, 0, 0, previous.energy3(), 0, 0,
                previous.kilowattHrRate(), previous.vat(), 0, previous.ipAddress());
    }

    public ElectricalRow createFromCsvRecord(CSVRecord record, DateTimeFormatter formatter) {
        // Same as before
        return new ElectricalRow(
                Integer.parseInt(record.get("ID")),
                record.get("DeviceID"),
                LocalDateTime.parse(record.get("Time"), formatter),
                Double.parseDouble(record.get("Voltage1")),
                Double.parseDouble(record.get("Current1")),
                Double.parseDouble(record.get("Power1")),
                Double.parseDouble(record.get("Energy1")),
                Double.parseDouble(record.get("Frequency1")),
                Double.parseDouble(record.get("PowerFactor1")),
                Double.parseDouble(record.get("Voltage2")),
                Double.parseDouble(record.get("Current2")),
                Double.parseDouble(record.get("Power2")),
                Double.parseDouble(record.get("Energy2")),
                Double.parseDouble(record.get("Frequency2")),
                Double.parseDouble(record.get("PowerFactor2")),
                Double.parseDouble(record.get("Voltage3")),
                Double.parseDouble(record.get("Current3")),
                Double.parseDouble(record.get("Power3")),
                Double.parseDouble(record.get("Energy3")),
                Double.parseDouble(record.get("Frequency3")),
                Double.parseDouble(record.get("PowerFactor3")),
                Double.parseDouble(record.get("KilowattHr Rate")),
                Double.parseDouble(record.get("VAT")),
                Double.parseDouble(record.get("Cost Data")),
                record.get("IP Address")
        );
    }
}