package com.webnobis.truebackup;

import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.read.DefaultReader;
import com.webnobis.truebackup.repair.DefaultRepairer;
import com.webnobis.truebackup.repair.Repairer;
import com.webnobis.truebackup.verify.DefaultVerifier;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Backup factory
 *
 * @author Steffen Nobis
 */
public class BackupFactory {

    static final AtomicReference<BackupFactory> factoryRef = new AtomicReference<>();
    private static final String FULL_FACTORY_CLASS_NAME = "com.webnobis.truebackup.FullBackupFactory";

    /**
     * If available, gets the full backup factory, otherwise the default backup factory
     *
     * @return the backup factory
     */
    public static BackupFactory instance() {
        return factoryRef.updateAndGet(factory -> Optional.ofNullable(factory).orElseGet(BackupFactory::create));
    }

    private static BackupFactory create() {
        try {
            return (BackupFactory) Class.forName(FULL_FACTORY_CLASS_NAME).getConstructor().newInstance();
        } catch (Exception e) {
            return new BackupFactory();
        }
    }

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
    public Backup<Bundle<Path>> of(Path master, Path copy, boolean repair, Path archiveDirForInvalidFileIfItShouldNotExist) {
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
     * @throws UnsupportedOperationException because it's the default backup factory
     */
    public Backup<List<Path>> of(List<Path> dirs, boolean repair, Path archiveDirForInvalidFileIfItShouldNotExist, String firstLevelSubDirsFilterRegEx) {
        throw new UnsupportedOperationException("only available at full version");
    }

}
