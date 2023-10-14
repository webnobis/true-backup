package com.webnobis.truebackup.repair;

import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.verify.DefaultVerifier;
import com.webnobis.truebackup.verify.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Abstract repairer of invalid files
 *
 * @author Steffen Nobis
 */
public abstract class AbstractRepairer implements Repairer {

    private static final Logger log = LoggerFactory.getLogger(AbstractRepairer.class);

    private static final String FILE_DEL_EXT_FORMAT = "_yyyyMMddHHmmss";
    final String fileDelExt;
    private final Path archiveDirForInvalidFileIfItShouldNotExist;
    private final Verifier<Bundle<Path>> verifier;

    /**
     * If valid file doesn't exist, invalid file will be moved to archive dir with date time file name extension, or deleted if null
     *
     * @param archiveDirForInvalidFileIfItShouldNotExist if valid file doesn't exist, moves the invalid file to archive dir or if null deletes the invalid file
     * @see AbstractRepairer#moveToArchiveOrDelete(Path)
     */
    protected AbstractRepairer(Path archiveDirForInvalidFileIfItShouldNotExist) {
        this.archiveDirForInvalidFileIfItShouldNotExist = archiveDirForInvalidFileIfItShouldNotExist;
        verifier = new DefaultVerifier();
        fileDelExt = archiveDirForInvalidFileIfItShouldNotExist != null ? DateTimeFormatter.ofPattern(FILE_DEL_EXT_FORMAT).format(LocalDateTime.now()) : null;
    }

    /**
     * Creates the invalid file from the content of the valid file
     *
     * @param invalidFile the invalid file
     * @throws IOException if creating failed
     */
    protected static void create(InvalidFile invalidFile) throws IOException {
        Objects.requireNonNull(invalidFile, "invalid file is null");
        Path valid = Objects.requireNonNull(invalidFile.valid(), "valid file is null");
        Path invalid = Objects.requireNonNull(invalidFile.invalid(), "invalid file is null");
        Files.copy(valid, invalid, StandardCopyOption.REPLACE_EXISTING);
        log.info("invalid file {} copied from {}", invalid, valid);
    }

    @Override
    public Stream<InvalidFile> repair(InvalidFile invalidFile) {
        if (invalidFile == null) {
            return Stream.empty();
        }

        try {
            if (invalidFile.shouldBeCreated()) {
                create(invalidFile);
            } else if (invalidFile.shouldBeDeleted()) {
                moveToArchiveOrDelete(invalidFile.invalid());
            } else {
                repairBytes(invalidFile);
            }
            return verifier.verify(new Bundle<>(invalidFile.valid(), invalidFile.invalid()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Stream.of(invalidFile);
        }
    }

    /**
     * Repairs the invalid file
     *
     * @param invalidFile the invalid file
     * @throws IOException if repairing failed
     */
    protected abstract void repairBytes(InvalidFile invalidFile) throws IOException;

    /**
     * Deletes the invalid file only if delete flag is true, otherwise renames it
     *
     * @param invalidFile the invalid file
     * @throws IOException if deleting or renaming failed
     * @see AbstractRepairer#AbstractRepairer(Path)
     */
    protected void moveToArchiveOrDelete(Path invalidFile) throws IOException {
        Objects.requireNonNull(invalidFile, "invalid file is null");
        if (archiveDirForInvalidFileIfItShouldNotExist == null) {
            Files.delete(invalidFile);
            log.info("invalid file {} deleted, because archive dir not set and valid file not exists", invalidFile);
        } else {
            String renamed = invalidFile.getFileName().toString().concat(fileDelExt);
            Path path = Optional.ofNullable(invalidFile.getRoot()).map(root -> root.relativize(invalidFile)).orElse(invalidFile).getParent();
            Path dir = archiveDirForInvalidFileIfItShouldNotExist.resolve(path);
            Files.createDirectories(dir);
            Files.move(invalidFile, dir.resolve(renamed));
            log.info("invalid file moved to {} and renamed to {}, because archive dir is set and valid file not exists", dir, renamed);
        }
    }
}
