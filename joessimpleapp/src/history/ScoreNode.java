package history;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JTextArea;

public final class ScoreNode extends ScoreEntry {
    private static final long serialVersionUID = 1L;
    private final ScoreNode next;

    public ScoreNode(String playerName, LocalDateTime dateTime, int score, int total, ScoreNode next) {
        super(playerName, dateTime, score, total);
        this.next = next;
    }

    @Override
    public String summary() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a");
        return playerName.toUpperCase() + ": " + dateTime.format(formatter) + " | " + score + "/" + total + " (" +
                (total == 0 ? "0" : String.format("%.1f", (score * 100.0) / total)) + "%)";
    }

    public void printHistory(JTextArea historyScores, int i) {
        historyScores.append("\n" + (++i) + ". " + summary());
        System.out.println(summary());
        if (next != null) {
            next.printHistory(historyScores, i);
        }
    }

    public ScoreNode getNext() {
        return next;
    }
}
