package history;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JTextArea;

/**
 * 
 * 
 * @author Joseph Ayoade
 * ScoreNode class is serializable, it uses recursion to create a history for the quiz taker
 * This objects are saved using ScoreStorageMultiUser class
 *
 */
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
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a");
        return playerName.toUpperCase() + ": " + dateTime.format(formatter) + " | " + score + "/" + total + " (" +
                (total == 0 ? "0" : String.format("%.1f", (score * 100.0) / total)) + "%)";
    }

    public void printHistory(JTextArea historyScores, int i) {
    	historyScores.append("\n"+(++i)+". "+summary());
        System.out.println(summary());
        if (next != null) {
        	next.printHistory(historyScores, i);
        }
      
    }



    public ScoreNode getNext() {
        return next;
    }
}
