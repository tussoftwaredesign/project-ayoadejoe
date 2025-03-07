package watts;

import java.time.LocalDateTime;
import org.apache.commons.csv.CSVRecord;

public record ElectricalRow(int id, String deviceId, LocalDateTime time, double voltage1, double current1, double power1,
        double energy1, double frequency1, double powerFactor1, double voltage2, double current2,
        double power2, double energy2, double frequency2, double powerFactor2, double voltage3,
        double current3, double power3, double energy3, double frequency3, double powerFactor3,
        double kilowattHrRate, double vat, double costData, String ipAddress) {
}
