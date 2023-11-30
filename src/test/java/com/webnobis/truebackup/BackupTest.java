package com.webnobis.truebackup;

import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.read.DefaultReader;
import com.webnobis.truebackup.read.Reader;
import com.webnobis.truebackup.repair.DefaultRepairer;
import com.webnobis.truebackup.repair.Repairer;
import com.webnobis.truebackup.verify.DefaultVerifier;
import com.webnobis.truebackup.verify.Verifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BackupTest {

    private static final Path DIRS = Path.of("dirs");

    private static final Path FILE = Path.of("file.txt");

    private static final InvalidFile INVALID_FILE = new InvalidFile(null, null, null);

    private static final Repairer REPAIRER = Stream::of;

    private static Reader<Path> reader;

    private static Verifier<Path> verifier;

    private Backup<Path> backup;

    @BeforeAll
    static void setUpAll() {
        reader = path -> {
            assertSame(DIRS, path);
            return Stream.of(FILE);
        };
        verifier = path -> {
            assertSame(FILE, path);
            return Stream.of(INVALID_FILE);
        };
    }

    @BeforeEach
    void setUp() {
        BackupFactory.factoryRef.set(new BackupFactory());
        backup = new Backup<>(DIRS, reader, verifier, REPAIRER);
    }

    @Test
    void ofDefault() {
        Path master = Path.of("master");
        Path copy = Path.of("copy");
        Backup<Bundle<Path>> defaultBackup = Backup.of(master, copy, true, null);

        Bundle<Path> dirs = defaultBackup.dirs();
        assertNotNull(dirs);
        assertEquals(master, dirs.master());
        assertEquals(copy, dirs.copy());
        assertEquals(DefaultReader.class, defaultBackup.reader().getClass());
        assertEquals(DefaultVerifier.class, defaultBackup.verifier().getClass());
        assertEquals(DefaultRepairer.class, defaultBackup.repairer().getClass());

        assertSame(Repairer.doesNothing(), Backup.of(master, copy, false, null).repairer());
    }

    @Test
    void ofFullFailed() {
        assertThrows(UnsupportedOperationException.class, () -> Backup.of(null, true, null, null));
    }

    @Test
    void ofFullIfOverwritten() {
        Backup<List<Path>> backup = new Backup<>(null, null, null, null);
        BackupFactory.factoryRef.set(new BackupFactory() {
            @Override
            public Backup<List<Path>> of(List<Path> dirs, boolean repair, Path archiveDirForInvalidFileIfItShouldNotExist, String firstLevelSubDirsFilterRegEx) {
                return backup;
            }
        });
        assertSame(backup, Backup.of(null, true, null, null));
    }

    @Test
    void backup() {
        List<InvalidFile> list = backup.backup();
        assertSame(1, list.size());
        assertSame(INVALID_FILE, list.iterator().next());
    }

    @Test
    void backupMultiThreaded() {
        int max = 100;
        String prefix = "the-good-prefix-";
        List<InvalidFile> list = new Backup<Integer>(max, i -> IntStream.range(0, i).boxed().parallel(), i -> Stream.of(new InvalidFile(Path.of(prefix + i), null, Collections.emptyList())), REPAIRER).backup();
        assertSame(max, list.size());
        list.forEach(file -> {
            assertTrue(file.invalid().getFileName().toString().startsWith(prefix));
            assertNull(file.valid());
            assertTrue(file.bytes().isEmpty());
        });
    }

    @Test
    void dirs() {
        assertSame(DIRS, backup.dirs());
    }

    @Test
    void reader() {
        assertSame(reader, backup.reader());
    }

    @Test
    void verifier() {
        assertSame(verifier, backup.verifier());
    }

    @Test
    void repairer() {
        assertSame(REPAIRER, backup.repairer());
    }
}