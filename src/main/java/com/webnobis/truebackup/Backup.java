package com.webnobis.truebackup;

import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.progress.Progress;
import com.webnobis.truebackup.progress.ProgressLog;
import com.webnobis.truebackup.read.Reader;
import com.webnobis.truebackup.repair.Repairer;
import com.webnobis.truebackup.verify.Verifier;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * True backup<br>
 * Reads all files of each directory and builds backup files bundles.<br>
 * Verifies the files bundles to find invalid bytes followed by invalid files.<br>
 * Optional repairs the invalid files, depending on the bound valid files.<br>
 *
 * @param dirs     all directories
 * @param reader   the reader
 * @param verifier the verifier
 * @param repairer the repairer
 * @param <T>      the bundle type
 * @author Steffen Nobis
 */
public record Backup<T>(T dirs, Reader<T> reader, Verifier<T> verifier,
                        Repairer repairer) {

    /**
     * Default backup<br>
     * It verifies all files of master and copy directory byte-by-byte.<br>
     * If repair is switched on, it repairs through file overwriting.
     * Only if repair is switched on, not existing copies were created.
     *
     * @param master                                     the master directory
     * @param copy                                       the copy directory
     * @param repair                                     repair is switched on, if true
     * @param archiveDirForInvalidFileIfItShouldNotExist if valid file doesn't exist, moves the invalid file to archive dir or if null deletes the invalid file
     * @return backup instance
     */
    static Backup<Bundle<Path>> of(Path master, Path copy, boolean repair, Path archiveDirForInvalidFileIfItShouldNotExist) {
        return BackupFactory.instance().of(master, copy, repair, archiveDirForInvalidFileIfItShouldNotExist);
    }

    /**
     * Full version backup, only available at full version<br>
     * It verifies all files of listed directories byte-by-byte via majority principle voting.<br>
     * If repair is switched on, it repairs byte-by-byte, irrelevant where the valid byte comes from.<br>
     * Only if repair is switched on, not existing directories and files were created.
     *
     * @param dirs                                       all directories
     * @param repair                                     repair is switched on, if true
     * @param archiveDirForInvalidFileIfItShouldNotExist if valid file doesn't exist, moves the invalid file to archive dir or if null deletes the invalid file
     * @param firstLevelSubDirsFilterRegEx               if not null, only matching first level subdirectories of dirs were used
     * @return backup instance
     * @throws UnsupportedOperationException if it's the default backup factory
     */
    static Backup<List<Path>> of(List<Path> dirs, boolean repair, Path archiveDirForInvalidFileIfItShouldNotExist, String firstLevelSubDirsFilterRegEx) {
        return BackupFactory.instance().of(dirs, repair, archiveDirForInvalidFileIfItShouldNotExist, firstLevelSubDirsFilterRegEx);
    }

    /**
     * Verifies all files and repairs all invalid files, if repair is switched on
     *
     * @return all invalid files at the end, should be empty
     * @throws UncheckedIOException if the backup failed
     */
    public List<InvalidFile> backup() {
        Objects.requireNonNull(verifier, "verifier is null");
        Objects.requireNonNull(repairer, "repairer is null");
        Progress progress = new ProgressLog();
        return Objects.requireNonNull(reader, "reader is null").read(dirs).map(progress::read)
                .parallel().map(verifier::verify).flatMap(progress::verified).map(progress::repair)
                .map(repairer::repair).flatMap(progress::repaired).toList();
    }
}
