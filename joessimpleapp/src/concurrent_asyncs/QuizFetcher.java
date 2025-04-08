package concurrent_asyncs;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONArray;
import org.json.JSONObject;

import enum_oops2.QuizCategory;
import nio_fileoperations.DataSaver;
import record_pojos.QuizData;

public class QuizFetcher {

    private static final String QUIZ_API = "https://the-trivia-api.com/v2/questions?categories=history,science&limit=5";
    
    public static QuizData fetchQuiz() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(QUIZ_API))
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray jsonArray = new JSONArray(response.body());

            JSONObject obj = jsonArray.getJSONObject(ThreadLocalRandom.current().nextInt(jsonArray.length()));
            String question = obj.getJSONObject("question").getString("text");
            String correct = obj.getString("correctAnswer");
            JSONArray incorrectArray = obj.getJSONArray("incorrectAnswers");

            List<String> options = new ArrayList<>();
            for (int i = 0; i < incorrectArray.length(); i++) {
                options.add(incorrectArray.getString(i));
            }
            options.add(correct);
            Collections.shuffle(options);
            
            QuizCategory category = QuizCategory.fromString(obj.getString("category"));
            
            QuizData quizData = new QuizData(question, options, correct, category);
            quizSaver.appendOne(quizData); 
            
            return quizData;

        } catch (Exception e) {
            System.err.println("Quiz fetch error: " + e.getMessage());
            return new QuizData("Error loading quiz", List.of(), "", null);
        }
    }
    
    private static final DataSaver<QuizData> quizSaver = new DataSaver<>(
    	    "quizzes.json",
    	    quiz -> String.format(
    	        "{\"question\":\"%s\", \"correctAnswer\":\"%s\", \"category\":\"%s\", \"options\":%s}",
    	        quiz.question(), quiz.correctAnswer(), quiz.category(), new JSONArray(quiz.options())
    	    ),
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
    	    }
    	);
}
