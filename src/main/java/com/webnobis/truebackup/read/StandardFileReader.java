package com.webnobis.truebackup.read;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Standard file reader
 *
 * @param firstSubDirFilterRegEx optional reg-ex to read only matching first level sub dirs
 * @author steffen nobis
 */
public record StandardFileReader(String firstSubDirFilterRegEx) implements Reader<Path> {

    private static final Logger log = LoggerFactory.getLogger(StandardFileReader.class);

    private static Stream<Path> relativizeFiles(Path dir, Set<String> firstLevel) {
        if (dir == null || Files.notExists(dir)) {
            return Stream.empty();
        }
        if (Files.isRegularFile(dir)) {
            return Stream.of(dir.getFileName());
        }
        try {
            return Files.walk(dir).filter(path -> firstLevel.stream().anyMatch(path.toString()::startsWith)).filter(Files::isRegularFile).map(dir::relativize);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Read all files from each dir and groups by dir
     *
     * @param dirs the dirs
     * @return list file stream, each dir
     * @see #firstSubDirFilterRegEx()
     */
    @Override
    public Stream<List<Path>> read(List<Path> dirs) {
        if (!Objects.requireNonNull(dirs, "dirs is null").stream().allMatch(Files::isDirectory)) {
            RuntimeException e = new IllegalStateException("only dirs are allowed, but found " + dirs);
            log.error(e.getMessage(), e);
            throw e;
        }

        Set<String> firstLevel = readFirstLevel(dirs);
        return dirs.stream().parallel().flatMap(dir -> relativizeFiles(dir, firstLevel))
                .distinct()
                .map(file -> dirs.stream().map(dir -> dir.resolve(file)).toList());
    }

    private Set<String> readFirstLevel(List<Path> dirs) {
        return dirs.stream().flatMap(dir -> {
                    try {
                        // read first level
                        return Files.isDirectory(dir) ? Files.list(dir) : Stream.of(dir);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        throw new UncheckedIOException(e);
                    }
                }).filter(this::firstSubDirFilterRegEx)
                .map(Path::toString).collect(Collectors.toUnmodifiableSet());
    }

    private boolean firstSubDirFilterRegEx(Path firstLevel) {
        return Optional.ofNullable(firstSubDirFilterRegEx).filter(unused -> Files.isDirectory(firstLevel)).map(firstLevel.getFileName().toString()::matches).orElse(true);
    }
}
