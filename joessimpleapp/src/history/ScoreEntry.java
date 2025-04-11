package history;

import java.io.Serializable;
import java.time.LocalDateTime;

public sealed abstract class ScoreEntry implements Serializable
    permits ScoreNode {
    
    protected final String playerName;
    protected final LocalDateTime dateTime;
    protected final int score;
    protected final int total;

    protected ScoreEntry(String playerName, LocalDateTime dateTime, int score, int total) {
        this.playerName = playerName;
        this.dateTime = dateTime;
        this.score = score;
        this.total = total;
    }

    public abstract String summary();
}
