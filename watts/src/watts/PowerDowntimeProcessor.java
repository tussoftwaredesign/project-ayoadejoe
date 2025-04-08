package watts;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PowerDowntimeProcessor {
    private final CsvDataReader reader;
    private final CsvDataWriter writer;
    private final GapDetector detector;
    private final RowFiller filler;
    private final AtomicInteger progress = new AtomicInteger(0);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PowerDowntimeProcessor(String inputFile, String outputFile) {
        this.reader = new CsvDataReader(inputFile, RowFactory.getInstance());
        this.writer = new CsvDataWriter(outputFile);
        this.detector = new GapDetector();
        this.filler = new RowFiller();
    }

    public void process() {
        executor.submit(() -> {
            try {
                ElectricalRow previous = null;
                Iterator<ElectricalRow> iterator = reader.readRows();
                while (iterator.hasNext()) {
                    ElectricalRow current = iterator.next();
                    if (previous != null) {
                        if (detector.isGap(previous, current)) {
                            List<ElectricalRow> missingRows = filler.fillMissingRows(previous, current.time());
                            writer.writeRows(missingRows);
                            progress.addAndGet(missingRows.size());
                        }
                    }
                    writer.writeRows(List.of(current));
                    progress.incrementAndGet();
                    previous = current;
                }
                System.out.println("Processing complete. Total rows processed: " + progress.get());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        });

        // Monitor progress in the main thread
        new Thread(() -> {
            while (!executor.isTerminated()) {
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                System.out.println("Progress: " + progress.get() + " rows");
            }
        }).start();
    }

    public int getProgress() {
        return progress.get();
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    public static void main(String[] args) {
        PowerDowntimeProcessor processor = new PowerDowntimeProcessor("input.csv", "output.csv");
        processor.process();
        // Add shutdown hook to ensure cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(processor::shutdown));
    }
}