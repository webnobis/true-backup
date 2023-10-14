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

    private Path archive;

    private Path valid;

    private Path invalid;

    private Path moved;

    private InvalidFile invalidFile;

    private TestAbstractRepairer repairer;

    @BeforeEach
    void setUp(Path dir) throws IOException {
        archive = dir.resolve("archive/1/2/3");
        valid = dir.resolve("a/b/c/valid.png");
        Files.createDirectories(valid.getParent());
        invalid = dir.resolve("x/y/invalid.png");
        Files.createDirectories(invalid.getParent());
        invalidFile = new InvalidFile(invalid, valid, null);
        repairer = new TestAbstractRepairer(archive);
        moved = archive.resolve(invalid.getRoot().relativize(invalid.getParent())).resolve("invalid.png" + repairer.fileDelExt);
    }

    @Test
    void repair() throws IOException {
        Files.write(valid, B);
        Files.write(invalid, new byte[]{-100});

        repairer.executor = invalidFile -> Files.write(invalidFile.invalid(), B);
        assertSame(0L, repairer.repair(invalidFile).count());
        assertArrayEquals(Files.readAllBytes(valid), Files.readAllBytes(invalid));
    }

    @Test
    void repairFailed() {
        repairer.executor = unused -> {
            throw new FileNotFoundException();
        };
        List<InvalidFile> list = repairer.repair(invalidFile).toList();
        assertSame(1, list.size());
        assertSame(invalidFile, list.iterator().next());
    }

    @Test
    void repairNull() {
        assertSame(0L, repairer.repair(null).count());
    }

    @Test
    void repairRuntimeException() {
        repairer.executor = unused -> {
            throw new RuntimeException();
        };
        assertThrows(RuntimeException.class, () -> repairer.repair(new InvalidFile(null, null, null)));
    }

    @Test
    void repairCreate() throws IOException {
        Files.write(valid, B);

        assertSame(0L, repairer.repair(invalidFile).count());
        assertArrayEquals(Files.readAllBytes(valid), Files.readAllBytes(invalid));
    }

    @Test
    void repairMoveToArchive() throws IOException {
        Files.write(invalid, B);

        assertSame(0L, repairer.repair(invalidFile).count());
        assertFalse(Files.exists(invalid));
        assertArrayEquals(B, Files.readAllBytes(moved));
    }

    @Test
    void repairDelete() throws IOException {
        Files.write(invalid, B);

        assertSame(0L, new TestAbstractRepairer(null).repair(invalidFile).count());
        assertFalse(Files.exists(invalid));
        assertFalse(Files.exists(moved));
    }

    private class TestAbstractRepairer extends AbstractRepairer {

        private Executor executor = unused -> fail("unexpected call"); // default

        TestAbstractRepairer(Path archiveDirForInvalidFileIfItShouldNotExist) {
            super(archiveDirForInvalidFileIfItShouldNotExist);
        }

        @Override
        protected void repairBytes(InvalidFile invalidFile) throws IOException {
            executor.execute(invalidFile);
        }

        @FunctionalInterface
        interface Executor {

            void execute(InvalidFile invalidFile) throws IOException;

        }

    }

}