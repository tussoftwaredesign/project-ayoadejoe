package enum_oops2;

public enum QuizCategory {
    HISTORY, SCIENCE, ART, GENERAL, ERROR;

    public static QuizCategory fromString(String raw) {
        return switch (raw.toLowerCase()) {
            case "science" -> SCIENCE;
            case "history" -> HISTORY;
            case "art" -> ART;
            case "general" -> GENERAL;
            default -> ERROR;
        };
    }
}
