package com.webnobis.truebackup.repair;

import com.webnobis.truebackup.model.InvalidFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Repairer of invalid files
 *
 * @author Steffen Nobis
 */
@FunctionalInterface
public interface Repairer {

    /**
     * File extension, used if delete option is false
     *
     * @see Repairer#deleteOrRenameInvalidFile(Path, boolean)
     */
    String FILE_DEL_EXT = ".del";

    /**
     * Deletes or renames the invalid file, depending on delete option
     *
     * @param invalidFile the invalid file
     * @param delete      the delete option
     * @throws IOException, if the deletion or renaming failed
     */
    static void deleteOrRenameInvalidFile(Path invalidFile, boolean delete) throws IOException {
        Objects.requireNonNull(invalidFile, "invalid file is null");
        if (delete) {
            Files.delete(invalidFile);
        } else {
            Files.move(invalidFile, invalidFile.getParent().resolve(invalidFile.getFileName().toString().concat(FILE_DEL_EXT)));
        }
    }

    /**
     * If valid file doesn't exist, invalid file should be deleted if true, otherwise renamed
     *
     * @return
     */
    default boolean deleteInvalidFileIfItShouldNotExist() {
        return false;
    }

    /**
     * Repairs the invalid file, through overwriting of invalid bytes with the valid bytes.<br>
     * If valid file doesn't exist, invalid file will be deleted if flag is true, otherwise renamed
     *
     * @param invalidFile the invalid file
     * @return empty stream, if repair was success, otherwise the stream of the same invalid file
     * @throws UncheckedIOException, if the repairing failed
     * @see Repairer#deleteInvalidFileIfItShouldNotExist()
     * @see Repairer#deleteOrRenameInvalidFile(Path, boolean)
     */
    Stream<InvalidFile> repair(InvalidFile invalidFile);

}
