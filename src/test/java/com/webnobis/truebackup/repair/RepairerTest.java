package com.webnobis.truebackup.repair;

import com.webnobis.truebackup.TempDirExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirExtension.class)
class RepairerTest {

    private static final byte[] B = {27};

    private Path file;

    @BeforeEach
    void setUp(Path dir) throws IOException {
        file = dir.resolve("test.png");
        Files.write(file, B);
    }

    @Test
    void deleteOrRenameInvalidFileRename(Path dir) throws IOException {
        Repairer.deleteOrRenameInvalidFile(file, false);
        assertFalse(Files.exists(file));
        Path renamed = dir.resolve("test.png" + Repairer.FILE_DEL_EXT);
        assertTrue(Files.exists(renamed));
        assertArrayEquals(B, Files.readAllBytes(renamed));
    }

    @Test
    void deleteOrRenameInvalidFileDelete(Path dir) throws IOException {
        Repairer.deleteOrRenameInvalidFile(file, true);
        assertFalse(Files.exists(file));
        assertFalse(Files.exists(dir.resolve("test.png" + Repairer.FILE_DEL_EXT)));
    }

    @Test
    void deleteInvalidFileIfItShouldNotExist() {
        assertFalse(((Repairer) unused -> null).deleteInvalidFileIfItShouldNotExist());
    }
}