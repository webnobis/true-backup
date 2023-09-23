package com.webnobis.truebackup;

import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.model.InvalidFile;
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
import java.util.stream.Stream;

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
     *
     * @param master                              the master directory
     * @param copy                                the copy directory
     * @param repair                              repair is switched on, if true
     * @param deleteInvalidFileIfItShouldNotExist if valid file doesn't exist, deletes the invalid file if true, otherwise renames it
     * @return backup instance
     */
    static Backup<Bundle<Path>> of(Path master, Path copy, boolean repair, boolean deleteInvalidFileIfItShouldNotExist) {
        return new Backup<Bundle<Path>>(new Bundle<>(master, copy), new DefaultReader(), new DefaultVerifier(), repair ? new DefaultRepairer(deleteInvalidFileIfItShouldNotExist) : Stream::of);
    }

    /**
     * Full version backup<br>
     * It verifies all files of listed directories byte-by-byte via majority principle voting.<br>
     * If repair is switched on, it repairs byte-by-byte, irrelevant where the valid byte comes from.
     *
     * @param dirs                                all directories
     * @param repair                              unused
     * @param deleteInvalidFileIfItShouldNotExist unused
     * @param repair                              repair is switched on, if true
     * @param deleteInvalidFileIfItShouldNotExist if valid file doesn't exist, deletes the invalid file if true, otherwise renames it
     * @return backup instance
     * @throws UnsupportedOperationException by now
     */
    static Backup<List<Path>> of(List<Path> dirs, boolean repair, boolean deleteInvalidFileIfItShouldNotExist) {
        throw new UnsupportedOperationException("only available at full version");
    }

    /**
     * Verifies all files and repairs all invalid files, if repair is switched on
     *
     * @return all invalid files at the end, should be empty
     * @throws UncheckedIOException, if the backup failed
     */
    public List<InvalidFile> backup() {
        Objects.requireNonNull(verifier, "verifier is null");
        Objects.requireNonNull(repairer, "repairer is null");
        return Objects.requireNonNull(reader, "reader is null").read(dirs)
                .parallel().flatMap(verifier::verify).flatMap(repairer::repair).toList();
    }
}
