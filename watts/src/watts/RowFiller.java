package watts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RowFiller {
    private final RowFactory rowFactory = RowFactory.getInstance();

    public List<ElectricalRow> fillMissingRows(ElectricalRow previous, LocalDateTime endTime) {
        List<ElectricalRow> rows = new ArrayList<>();
        LocalDateTime currentTime = previous.time().plusMinutes(1);
        int id = previous.id() - 1;

        while (currentTime.isBefore(endTime)) {
            rows.add(rowFactory.createDowntimeRow(id--, currentTime, previous));
            currentTime = currentTime.plusMinutes(1);
        }
        return rows;
    }
}