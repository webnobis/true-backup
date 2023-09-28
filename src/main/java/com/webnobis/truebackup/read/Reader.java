package com.webnobis.truebackup.read;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Files reader
 *
 * @param <T> bundle type
 * @author Steffen Nobis
 */
@FunctionalInterface
public interface Reader<T> {

    /**
     * Stream of all files with relative path
     *
     * @param dir the directory
     * @return all found files with relative path
     * @throws IOException, if the reading failed
     */
    static Stream<Path> relativizeFiles(Path dir) throws IOException {
        if (dir == null || Files.notExists(dir)) {
            return Stream.empty();
        }
        if (Files.isRegularFile(dir)) {
            return Stream.of(dir);
        }
        return Files.walk(dir).filter(Files::isRegularFile).map(dir::relativize);
    }

    /**
     * Unique stream of all file bundles, existing at least in one of the directories
     *
     * @param dirs all directories
     * @return all file bundles
     * @throws UncheckedIOException, if the reading failed
     * @see Reader#relativizeFiles(Path)
     */
    Stream<T> read(T dirs);
}
