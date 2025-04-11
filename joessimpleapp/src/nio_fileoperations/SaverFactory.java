
package nio_fileoperations;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import enum_oops2.QuizCategory;
import record_pojos.JokeData;
import record_pojos.QuizData;

public class SaverFactory {

    public static DataSaver<JokeData> createJokeSaver() {
        return new DataSaver<>("jokes.json", 
            joke -> String.format("{\"setup\":\"%s\", \"delivery\":\"%s\"}", joke.setup(), joke.delivery()),
            line -> {
                try {
                    JSONObject obj = new JSONObject(line);
                    return new JokeData(obj.getString("setup"), obj.getString("delivery"));
                } catch (Exception e) {
                    return new JokeData("Oops!", "Failed to parse joke.");
                }
            });
    }

    public static DataSaver<QuizData> createQuizSaver() {
        return new DataSaver<>("quizzes.json", 
            quiz -> String.format("{\"question\":\"%s\", \"correctAnswer\":\"%s\", \"category\":\"%s\", \"options\":%s}",
                                  quiz.question(), quiz.correctAnswer(), quiz.category(), new JSONArray(quiz.options())),
            line -> {
                try {
                    JSONObject obj = new JSONObject(line);
                    String question = obj.getString("question");
                    String correct = obj.getString("correctAnswer");
                    QuizCategory category = QuizCategory.fromString(obj.getString("category"));
                    JSONArray optionsArray = obj.getJSONArray("options");
                    List<String> options = new ArrayList<>();
                    for (int i = 0; i < optionsArray.length(); i++) {
                        options.add(optionsArray.getString(i));
                    }
                    return new QuizData(question, options, correct, category);
                } catch (Exception e) {
                    return new QuizData("Parsing error", List.of(), "", null);
                }
            });
    }
}
