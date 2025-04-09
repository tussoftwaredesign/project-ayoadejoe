package history;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

//using a map and recursion, the history of the quiz taker is stored and retrieved
public class ScoreStorageMultiUser {

    private static final String FILE_PATH = "data/all_score_histories.ser";

    public static void saveAll(Map<String, ScoreNode> allHistories) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(allHistories);
            System.out.println("All user histories saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, ScoreNode> loadAll() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<String, ScoreNode>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to load score history. Starting fresh.");
            return new HashMap<>();
        }
    }
}
