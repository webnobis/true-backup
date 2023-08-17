package com.webnobis.truebackup.verify;

import com.webnobis.truebackup.model.ReadFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStream {

    static Stream<ReadFile> read(Path dir) {
        if (dir == null || !Files.exists(dir) || !Files.isDirectory(dir)) {
            return Stream.empty();
        }

        try {
            return Files.walk(dir).parallel().filter(Files::isRegularFile).map(file -> new ReadFile(dir.relativize(file).toString(), file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
