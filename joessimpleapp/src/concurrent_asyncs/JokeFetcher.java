package concurrent_asyncs;

import java.net.URI;
import java.net.http.*;
import org.json.JSONObject;

import record_pojos.JokeData;


public class JokeFetcher extends AbstractFetcher<JokeData>{

    private static final String JOKE_API = "https://v2.jokeapi.dev/joke/Any?blacklistFlags=racist,sexist,explicit";

    public static JokeData fetchJoke() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(JOKE_API))
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            JSONObject obj = new JSONObject(response.body());

            String setup = obj.getString("setup");
            String delivery = obj.getString("delivery");

            return new JokeData(setup, delivery);

        } catch (Exception e) {
            System.err.println("Joke fetch error: " + e.getMessage());
            e.printStackTrace();
            return new JokeData("Oops...", "Couldn't fetch joke!");
        }
    }
    
    /**
     * Fetches a joke from the Jokeapi API.
     * 
     * @return a JokeData record with the loaded question and options
     */

	@Override
	public JokeData fetch() {
		  return new JokeFetcher().fetch();
	}
}
