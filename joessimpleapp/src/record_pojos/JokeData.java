package record_pojos;

public record JokeData(String setup, String delivery) {
    public String getFormattedJoke() {
        return "<html>" + setup + "<br><b>" + delivery + "</b></html>";
    }
}