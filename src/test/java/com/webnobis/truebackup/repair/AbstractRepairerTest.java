package com.webnobis.truebackup.repair;

import com.webnobis.truebackup.TempDirExtension;
import com.webnobis.truebackup.model.InvalidFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirExtension.class)
class AbstractRepairerTest {

    private static final byte[] B = {27, -1, 42};

    private Path valid;

    private Path invalid;

    private Path renamed;

    private InvalidFile invalidFile;

    private Repairer repairer;

    @BeforeEach
    void setUp(Path dir) {
        valid = dir.resolve("valid.png");
        invalid = dir.resolve("invalid.png");
        renamed = dir.resolve("invalid.png" + AbstractRepairer.FILE_DEL_EXT);
        invalidFile = new InvalidFile(invalid, valid, null);
        repairer = new AbstractRepairer(false) {
            @Override
            protected void repairBytes(InvalidFile invalidFile) {
                fail("unexpected call");
            }
        };
    }

    @Test
    void repair() throws IOException {
        Files.write(valid, B);
        Files.write(invalid, new byte[]{-100});

        assertSame(0L, new AbstractRepairer(false) {
            @Override
            protected void repairBytes(InvalidFile invalidFile) throws IOException {
                Files.write(invalidFile.invalid(), B);
            }
        }.repair(invalidFile).count());
        assertArrayEquals(Files.readAllBytes(valid), Files.readAllBytes(invalid));
    }

    @Test
    void repairFailed() {
        List<InvalidFile> list = new AbstractRepairer(false) {
            @Override
            protected void repairBytes(InvalidFile invalidFile) throws IOException {
                throw new FileNotFoundException();
            }
        }.repair(invalidFile).toList();
        assertSame(1, list.size());
        assertSame(invalidFile, list.iterator().next());
    }

    @Test
    void repairNull() {
        assertSame(0L, repairer.repair(null).count());
    }

    @Test
    void repairRuntimeException() {
        assertThrows(RuntimeException.class, () -> new AbstractRepairer(false) {

            @Override
            protected void repairBytes(InvalidFile invalidFile) {
                throw new RuntimeException();
            }
        }.repair(new InvalidFile(null, null, null)));
    }

    @Test
    void repairCreate() throws IOException {
        Files.write(valid, B);

        assertSame(0L, repairer.repair(invalidFile).count());
        assertArrayEquals(Files.readAllBytes(valid), Files.readAllBytes(invalid));
    }

    @Test
    void repairRename() throws IOException {
        Files.write(invalid, B);

        assertSame(0L, repairer.repair(invalidFile).count());
        assertFalse(Files.exists(invalid));
        assertArrayEquals(B, Files.readAllBytes(renamed));
    }

    @Test
    void repairDelete() throws IOException {
        Files.write(invalid, B);

        assertSame(0L, new AbstractRepairer(true) {
            @Override
            protected void repairBytes(InvalidFile invalidFile) {
                fail("unexpected call");
            }
        }.repair(invalidFile).count());
        assertFalse(Files.exists(invalid));
        assertFalse(Files.exists(renamed));
    }

}