package history;

import java.io.*;

public class ScoreStorageMultiUser {

    private static final String FILE_PATH = "data/score_history.ser";

    public static void saveScoreHistory(ScoreNode head) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(head);
            System.out.println("Score history saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ScoreNode loadScoreHistory() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (ScoreNode) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No previous score history found or failed to load.");
            return null;
        }
    }
}
