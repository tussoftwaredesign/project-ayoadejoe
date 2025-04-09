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
    
    /**
     * Fetches a quiz from the Trivia API.
     * 
     * @return a QuizData record with the loaded question and options
     */
    //Supplier-style takes nothing returns a record
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
            
            //hard-wire the category using enums
            QuizCategory category = QuizCategory.fromString(obj.getString("category"));
            
            QuizData quizData = new QuizData(question, options, correct, category);
            
            return quizData;

        } catch (Exception e) {
            System.err.println("Quiz fetch error: " + e.getMessage());
            return new QuizData("Error loading quiz", List.of(), "", null);
        }
    }
    

}
