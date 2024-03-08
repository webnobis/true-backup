package com.webnobis.truebackup;

import com.webnobis.truebackup.model.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record BackupFactory(String extensionsDir, String fullBackupClassName) implements BackupBuilder {

    public static final String EXTENSIONS_DIR = "extensions";

    static final String FULL_BACKUP_CLASS_NAME = "de.db.learn.optional.libs.full.FullBackup";

    private static final Logger log = LoggerFactory.getLogger(BackupFactory.class);

    public BackupFactory() {
        this(EXTENSIONS_DIR, FULL_BACKUP_CLASS_NAME);
    }

    @Override
    public Backup<?> createBackup(Commands commands) {
        try {
            URLClassLoader uc = URLClassLoader.newInstance(
                    Files.list(Paths.get(extensionsDir())).map(Path::toUri).map(uri -> {
                        try {
                            return uri.toURL();
                        } catch (MalformedURLException e) {
                            throw new UncheckedIOException(e);
                        }
                    }).toArray(i -> new URL[i]), BackupFactory.class.getClassLoader());

            Class<?> fullBackupClass = Class.forName(fullBackupClassName(), true, uc);
            Constructor<?> constructor = fullBackupClass.getConstructor(Commands.class);
            log.info("Full backup available. It will be created.");
            return Backup.class.cast(constructor.newInstance(commands));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            log.info("Full backup not available, fall back to standard backup.");
            log.info("If you want to use the full backup, please get the full backup jar from the author, place it in the folder {} and start the program again.", extensionsDir());
            return new StandardBackup(commands);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

}
