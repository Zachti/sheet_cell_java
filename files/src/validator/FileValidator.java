package validator;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class FileValidator {

    private FileValidator() {}

    public static String validateFilePath(String filePath) {
        Path path = Paths.get(filePath);
        Optional.ofNullable(path.getParent())
                .flatMap(parent -> Stream.iterate(parent, Objects::nonNull, Path::getParent)
                        .filter(p -> !Files.exists(p))
                        .findFirst())
                .ifPresent(nonExistentDir -> {
                    throw new InvalidPathException(nonExistentDir.toString(), "Parent directory does not exist.");
                });
        return filePath;
    }

    public static void validateFileExists(String filePath) {
        Optional.of(Files.exists(Paths.get(filePath)))
                .filter(Boolean::booleanValue)
                .orElseThrow(() -> new IllegalArgumentException("File does not exist."));
    }

}
