package nio_fileoperations;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataSaver<T> {
    private final Path filePath;
    private final Function<T, String> toJsonMapper;
    private final Function<String, T> fromJsonMapper;

    public DataSaver(String filename, Function<T, String> toJsonMapper, Function<String, T> fromJsonMapper) {
        this.filePath = Paths.get("data", filename);
        this.toJsonMapper = toJsonMapper;
        this.fromJsonMapper = fromJsonMapper;
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) Files.createFile(filePath);
        } catch (IOException e) {
            System.err.println("Error creating file: " + e.getMessage());
        }
    }

    public void appendOne(T item) {
        try {
            Files.writeString(filePath, toJsonMapper.apply(item) + System.lineSeparator(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Append failed: " + e.getMessage());
        }
    }

    public List<T> loadAll() {
        try {
            return Files.lines(filePath)
                    .map(fromJsonMapper)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Load failed: " + e.getMessage());
            return List.of();
        }
    }
}

