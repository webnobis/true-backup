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
import java.util.Objects;
import java.util.stream.Stream;


/**
 * Abstract repairer of invalid files
 *
 * @author Steffen Nobis
 */
public abstract class AbstractRepairer implements Repairer {

    /**
     * File extension, used if delete option is false
     *
     * @see AbstractRepairer#AbstractRepairer(boolean)
     */
    protected static final String FILE_DEL_EXT = ".del";
    private static final Logger log = LoggerFactory.getLogger(AbstractRepairer.class);
    private final boolean deleteInvalidFileIfItShouldNotExist;

    private final Verifier<Bundle<Path>> verifier;

    /**
     * If valid file doesn't exist, invalid file will be deleted if delete flag is true, otherwise renamed
     *
     * @param deleteInvalidFileIfItShouldNotExist delete flag
     * @see AbstractRepairer#deleteOrRename(Path)
     */
    protected AbstractRepairer(boolean deleteInvalidFileIfItShouldNotExist) {
        this.deleteInvalidFileIfItShouldNotExist = deleteInvalidFileIfItShouldNotExist;
        verifier = new DefaultVerifier();
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
                deleteOrRename(invalidFile.invalid());
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
     * @see AbstractRepairer#AbstractRepairer(boolean)
     */
    protected void deleteOrRename(Path invalidFile) throws IOException {
        Objects.requireNonNull(invalidFile, "invalid file is null");
        if (deleteInvalidFileIfItShouldNotExist) {
            Files.delete(invalidFile);
            log.info("invalid file {} deleted, because delete flag is true and valid file not exists", invalidFile);
        } else {
            String renamed = invalidFile.getFileName().toString().concat(FILE_DEL_EXT);
            Path file = Files.move(invalidFile, invalidFile.getParent().resolve(renamed));
            log.info("invalid file renamed to {}, because delete flag is false and valid file not exists", file);
        }
    }
}
