package com.webnobis.truebackup;

import com.webnobis.truebackup.model.Commands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BackupFactoryTest {

    private static final String TEST_EXTENSIONS = "test-extensions";

    private static final String TEST_EXTENSIONS_BACKUP_CLASS_NAME = "com.webnobis.truebackup.test.extensions.TestExtensionsBackup";

    private BackupFactory factory;

    @BeforeEach
    void setUp() {
        factory = new BackupFactory();
    }

    @Test
    void createBackup() {
        Backup<?> backup = new BackupFactory(".", "not.exist.Class").createBackup(new Commands(Collections.emptyList(), true, Path.of("nothing"), null));
        assertNotNull(backup);
        assertEquals(StandardBackup.class, backup.getClass());
    }

    @Test
    void createExtensionsBackup() {
        Backup<?> backup = new BackupFactory(TEST_EXTENSIONS, TEST_EXTENSIONS_BACKUP_CLASS_NAME).createBackup(new Commands(Collections.emptyList(), true, Path.of("nothing"), null));
        assertNotNull(backup);
        assertEquals(TEST_EXTENSIONS_BACKUP_CLASS_NAME, backup.getClass().getName());
    }

    @Test
    void extensionsDir() {
        assertSame(BackupFactory.EXTENSIONS_DIR, factory.extensionsDir());
    }

    @Test
    void fullBackupClassName() {
        assertSame(BackupFactory.FULL_BACKUP_CLASS_NAME, factory.fullBackupClassName());
    }
}