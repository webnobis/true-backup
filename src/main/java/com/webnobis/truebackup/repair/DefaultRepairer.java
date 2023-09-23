package com.webnobis.truebackup.repair;

import com.webnobis.truebackup.model.InvalidFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * Default repairer of invalid files
 *
 * @param deleteInvalidFileIfItShouldNotExist delete flag
 * @author Steffen Nobis
 * @see Repairer#deleteInvalidFileIfItShouldNotExist()
 */
public record DefaultRepairer(boolean deleteInvalidFileIfItShouldNotExist) implements Repairer {

    private static final Logger log = LoggerFactory.getLogger(DefaultRepairer.class);

    @Override
    public Stream<InvalidFile> repair(InvalidFile invalidFile) {
        if (invalidFile == null) {
            return Stream.empty();
        }

        Path valid = invalidFile.valid();
        Path invalid = invalidFile.invalid();
        if (Files.exists(invalid) && !Files.isWritable(invalid)) {
            UncheckedIOException e = new UncheckedIOException(new IOException(invalid.toString() + " is readonly"));
            log.error(e.getMessage(), e);
            throw e;
        }
        try {
            if (!Files.exists(valid)) {
                Repairer.deleteOrRenameInvalidFile(invalid, deleteInvalidFileIfItShouldNotExist);
            } else {
                Files.copy(valid, invalid, StandardCopyOption.REPLACE_EXISTING);
            }
            return Stream.empty();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Stream.of(new InvalidFile(invalid, valid, invalidFile.bytes()));
        }
    }
}
