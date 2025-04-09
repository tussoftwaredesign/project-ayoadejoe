package record_pojos;

public class ScoreTracker {
    private int totalAttempts = 0;
    private int correctAnswers = 0;

    public void recordAttempt(boolean correct) {
        totalAttempts++;
        if (correct) correctAnswers++;
    }

    public int getTotal() { return totalAttempts; }
    public int getCorrect() { return correctAnswers; }

    public double getAccuracy() {
        return totalAttempts == 0 ? 0 : (correctAnswers * 100.0) / totalAttempts;
    }

    @Override
    public String toString() {
        return  correctAnswers + "/" + totalAttempts +
               " (" + String.format("%.1f", getAccuracy()) + "%)";
    }
}
