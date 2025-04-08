package concurrent_asyncs;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.SwingUtilities;

public class AsyncFetcher<T> {
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public void fetch(Supplier<T> task, Consumer<T> callback) {
        executor.submit(() -> {
            T result = task.get();
            SwingUtilities.invokeLater(() -> callback.accept(result)); // UI-safe
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
