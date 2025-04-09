package enum_oops2;

/*
 * 
 * In this enum, I showed how to use unnamed variables in a switch statement
 * This is commented because  I am using 
 */
public enum Java22_QuizCategory {
	HISTORY, SCIENCE, ART, GENERAL, ERROR;

    public static Java22_QuizCategory fromString(String raw) {
        return switch (raw.toLowerCase()) {
            case "science" -> SCIENCE;
            case "history" -> HISTORY;
            case "art" -> ART;
            case "general" -> GENERAL;
            //default -> _;	//<-- Unnamed variable
		default -> throw new IllegalArgumentException("Unexpected value: " + raw.toLowerCase());
        };
    }
}