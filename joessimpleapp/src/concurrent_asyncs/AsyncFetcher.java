package concurrent_asyncs;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.SwingUtilities;

public class AsyncFetcher<T> {
	
	/**Use ExecutorService to create fixed thread pool so as to branch out of the main thread and get an async
	 * Using a Supplier, we can always return the object that this executor thread goes to get
	 * Using a Consumer, we can always implement a callback to notify the main thread that the result is now available
	 * In this instance, we would be using it to return the quizdata and the jokedata
	*/
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
