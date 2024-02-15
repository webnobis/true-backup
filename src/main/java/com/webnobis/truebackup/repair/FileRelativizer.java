package com.webnobis.truebackup.repair;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * File relativizer
 *
 * @author Steffen Nobis
 */
public interface FileRelativizer {

    /**
     * Extracts the relative path of both same naming files
     *
     * @param file1 file of 1st root
     * @param file2 file of 2nd root
     * @return relative file path
     */
    static Path relativize(Path file1, Path file2) {
        List<List<String>> files = Stream.of(Objects.requireNonNull(file1, "file1 is null"),
                        Objects.requireNonNull(file2, "file2 is null"))
                .map(Path::toString).map(path -> Arrays.stream(path.split(File.separator)).toList())
                .toList();
        int index = IntStream.rangeClosed(1, files.stream().mapToInt(List::size)
                        .max().orElseThrow()).takeWhile(i -> files.stream().filter(list -> list.size() - i >= 0)
                        .map(list -> list.get(list.size() - i)).distinct().count() < files.size())
                .max().orElse(0);
        return Path.of(files.stream().findAny().map(list -> list.subList(list.size() - index, list.size()).stream()
                .collect(Collectors.joining(File.separator))).orElseThrow());
    }

}
