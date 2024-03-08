package com.webnobis.truebackup;

import com.webnobis.truebackup.model.Commands;
import com.webnobis.truebackup.read.StandardFileReader;
import com.webnobis.truebackup.repair.Repairer;
import com.webnobis.truebackup.repair.StandardFileRepairer;
import com.webnobis.truebackup.verify.StandardFileVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StandardBackupTest {

    private static final Commands COMMANDS = new Commands(List.of(Path.of("dir1"), Path.of("dir2")), true, Path.of("archive"), null);

    private static final Commands COMMANDS_MORE = new Commands(List.of(Path.of("dir1"), Path.of("dir2"), Path.of("dir3")), true, Path.of("archive"), null);

    private static final Commands COMMANDS_LESS = new Commands(Collections.singletonList(Path.of("dir")), true, Path.of("archive"), null);

    private static final Commands COMMANDS_REPAIR_OFF = new Commands(Collections.singletonList(Path.of("dir")), false, Path.of("archive"), null);

    private Backup<Long> backup;

    @BeforeEach
    void setUp() {
        backup = new StandardBackup(COMMANDS);
    }

    @Test
    void dirs() {
        assertSame(COMMANDS.dirs(), backup.dirs());
    }

    @Test
    void dirsLimited() {
        assertEquals(COMMANDS_MORE.dirs().subList(0, 2), new StandardBackup(COMMANDS_MORE).dirs());

        assertSame(COMMANDS_LESS.dirs(), new StandardBackup(COMMANDS_LESS).dirs());
    }

    @Test
    void reader() {
        assertEquals(StandardFileReader.class, backup.reader().getClass());
    }

    @Test
    void verifier() {
        assertEquals(StandardFileVerifier.class, backup.verifier().getClass());
    }

    @Test
    void repairer() {
        assertEquals(StandardFileRepairer.class, backup.repairer().getClass());
    }

    @Test
    void repairerOff() {
        Class<? extends Repairer> repairerClass = new StandardBackup(COMMANDS_REPAIR_OFF).repairer().getClass();
        assertNotEquals(StandardFileRepairer.class, repairerClass);
        assertTrue(Repairer.class.isAssignableFrom(repairerClass));
    }
}