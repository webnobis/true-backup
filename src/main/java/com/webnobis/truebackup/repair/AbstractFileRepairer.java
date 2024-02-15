package com.webnobis.truebackup.repair;

import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.verify.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class AbstractFileRepairer<T> implements Repairer<InvalidFile<T>> {

    private static final Logger log = LoggerFactory.getLogger(AbstractFileRepairer.class);

    private final Path archiveDir;

    private final Verifier<InvalidFile<T>, Path> verifier;

    protected AbstractFileRepairer(Path archiveDir, Verifier<InvalidFile<T>, Path> verifier) {
        this.archiveDir = Objects.requireNonNull(archiveDir, "archive dir is null");
        this.verifier = Objects.requireNonNull(verifier, "verifier is null");
    }

    protected static void copy(Path fromFile, Path toFile) throws IOException {
        copyOrMove(fromFile, toFile, false);
    }

    protected static void move(Path fromFile, Path toFile) throws IOException {
        copyOrMove(fromFile, toFile, true);
    }

    private static void copyOrMove(Path fromFile, Path toFile, boolean move) throws IOException {
        if (!Files.exists(Objects.requireNonNull(fromFile, "from file is null"))) {
            throw new FileNotFoundException(fromFile.toString());
        }

        Path toDir = Objects.requireNonNull(toFile, "to file is null").getParent();
        if (!Files.exists(toDir)) {
            Files.createDirectories(toDir);
        }
        if (move) {
            Files.move(fromFile, toFile,
                    StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.copy(fromFile, toFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    protected abstract void repair(Path invalidFile, Path validFile, T invalidBytes) throws IOException;

    @Override
    public Stream<InvalidFile<T>> repair(InvalidFile<T> file) {
        try {
            Path invalidFile = Objects.requireNonNull(file, "file is null").invalidFile();
            Path validFile = file.validFile();
            if (file.shouldCreate()) {
                copy(validFile, invalidFile);
            } else {
                Path targetFile = archiveDir.resolve(FileRelativizer.relativize(invalidFile, validFile)).resolve(invalidFile.getFileName());
                if (file.shouldRepair()) {
                    copy(invalidFile, targetFile);
                    repair(invalidFile, validFile, file.invalidBytes());
                } else {
                    move(invalidFile, targetFile);
                }
            }
            log.info("Invalid file successful {} repaired", invalidFile);
            return verifier.verify(List.of(invalidFile, validFile));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Stream.of(file);
        }
    }

}
