package history;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ScoreNode implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String playerName;
    private final LocalDateTime dateTime;
    private final int score;
    private final int total;
    private final ScoreNode next;

    public ScoreNode(String playerName, LocalDateTime dateTime, int score, int total, ScoreNode next) {
        this.playerName = playerName;
        this.dateTime = dateTime;
        this.score = score;
        this.total = total;
        this.next = next;
    }

    public String summary() {
        return playerName + " | " + dateTime + " | " + score + "/" + total + " (" +
                (total == 0 ? "0" : String.format("%.1f", (score * 100.0) / total)) + "%)";
    }

    public void printHistory() {
        System.out.println(summary());
        if (next != null) next.printHistory();
    }

    public ScoreNode getNext() {
        return next;
    }
}
