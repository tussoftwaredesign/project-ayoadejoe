package watts;

import java.time.Duration;
import java.util.function.Predicate;

public class GapDetector {
    private static final long MINUTE_IN_SECONDS = 60;
    private static final double ENERGY_THRESHOLD = 0.02;

    public boolean isGap(ElectricalRow previous, ElectricalRow current) {
        Predicate<Duration> timeGapPredicate = diff -> diff.getSeconds() > MINUTE_IN_SECONDS;
        Predicate<Double> energyDiffPredicate = diff -> diff < ENERGY_THRESHOLD;

        Duration timeDiff = Duration.between(previous.time(), current.time());
        double energy1Diff = Math.abs(current.energy1() - previous.energy1());
        double energy2Diff = Math.abs(current.energy2() - previous.energy2());
        double energy3Diff = Math.abs(current.energy3() - previous.energy3());

        return timeGapPredicate.test(timeDiff) &&
               energyDiffPredicate.test(energy1Diff) &&
               energyDiffPredicate.test(energy2Diff) &&
               energyDiffPredicate.test(energy3Diff);
    }
}