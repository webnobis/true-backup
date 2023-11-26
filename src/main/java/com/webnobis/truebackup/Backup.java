package com.webnobis.truebackup;

import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.progress.ProgressLog;
import com.webnobis.truebackup.read.DefaultReader;
import com.webnobis.truebackup.read.Reader;
import com.webnobis.truebackup.repair.DefaultRepairer;
import com.webnobis.truebackup.repair.Repairer;
import com.webnobis.truebackup.verify.DefaultVerifier;
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
        return new Backup<Bundle<Path>>(new Bundle<>(master, copy), new DefaultReader(), new DefaultVerifier(), repair ? new DefaultRepairer(archiveDirForInvalidFileIfItShouldNotExist) : Repairer.doesNothing());
    }

    /**
     * Full version backup<br>
     * It verifies all files of listed directories byte-by-byte via majority principle voting.<br>
     * If repair is switched on, it repairs byte-by-byte, irrelevant where the valid byte comes from.<br>
     * Only if repair is switched on, not existing directories and files were created.
     *
     * @param dirs                                       all directories
     * @param repair                                     repair is switched on, if true
     * @param archiveDirForInvalidFileIfItShouldNotExist if valid file doesn't exist, moves the invalid file to archive dir or if null deletes the invalid file
     * @param firstLevelSubDirsFilterRegEx               if not null, only matching first level subdirectories of dirs were used
     * @return backup instance
     * @throws UnsupportedOperationException by now
     */
    static Backup<List<Path>> of(List<Path> dirs, boolean repair, Path archiveDirForInvalidFileIfItShouldNotExist, String firstLevelSubDirsFilterRegEx) {
        throw new UnsupportedOperationException("only available at full version");
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
        ProgressLog progressBar = new ProgressLog();
        return Objects.requireNonNull(reader, "reader is null").read(dirs).map(progressBar::read)
                .parallel().map(verifier::verify).flatMap(progressBar::verified).map(progressBar::repair)
                .map(repairer::repair).flatMap(progressBar::repaired).toList();
    }
}
