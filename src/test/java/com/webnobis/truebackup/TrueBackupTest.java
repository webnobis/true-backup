package com.webnobis.truebackup;

import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.read.Reader;
import com.webnobis.truebackup.repair.Repairer;
import com.webnobis.truebackup.verify.Verifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirExtension.class)
class TrueBackupTest {

    private AtomicBoolean called;

    private BackupBuilder builder;

    private TrueBackup backup;

    @BeforeEach
    void setUp() {
        called = new AtomicBoolean();
        builder = commands -> new Backup<Void>() {
            @Override
            public List<Path> dirs() {
                return null;
            }

            @Override
            public Reader<Path> reader() {
                return null;
            }

            @Override
            public Verifier<InvalidFile<Void>, Path> verifier() {
                return null;
            }

            @Override
            public Repairer<InvalidFile<Void>> repairer() {
                return null;
            }

            @Override
            public boolean backup() {
                called.set(true);
                return Optional.ofNullable(commands.firstSubDirFilterRegEx()).isPresent();
            }
        };
        backup = new TrueBackup(builder);
    }

    @Test
    void main(Path tmpDir) {
        TrueBackup.main(new String[]{"-b", tmpDir.toString()});
        assertFalse(called.get());
    }

    @Test
    void backup() {
        assertTrue(backup.backup(new String[]{"-b", "dir", "-s", "filter"}));

        assertFalse(backup.backup(new String[]{"-b", "dir"}));
    }

    @Test
    void backupBuilder() {
        assertSame(builder, backup.backupBuilder());
    }
}