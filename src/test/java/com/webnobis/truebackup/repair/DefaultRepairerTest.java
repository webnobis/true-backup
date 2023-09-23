package com.webnobis.truebackup.repair;

import com.webnobis.truebackup.TempDirExtension;
import com.webnobis.truebackup.model.InvalidFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.webnobis.truebackup.repair.Repairer.FILE_DEL_EXT;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirExtension.class)
class DefaultRepairerTest {

    private static final byte[] B = {-1, 9, 42, 127, -99};

    private Path invalid;

    private Path valid;

    private Repairer repairer;

    @BeforeEach
    void setUp(Path dir) {
        invalid = dir.resolve("test.png");
        valid = dir.resolve("other.jpg");
        repairer = new DefaultRepairer(false);
    }

    @Test
    void repairBothExists() throws IOException {
        Files.write(invalid, new byte[]{6});
        Files.write(valid, B);
        assertTrue(repairer.repair(new InvalidFile(invalid, valid, null)).toList().isEmpty());
        assertArrayEquals(B, Files.readAllBytes(invalid));
    }

    @Test
    void repairInvalidNotExist() throws IOException {
        Files.write(valid, B);
        assertTrue(repairer.repair(new InvalidFile(invalid, valid, null)).toList().isEmpty());
        assertArrayEquals(B, Files.readAllBytes(invalid));
    }

    @Test
    void repairValidNotExistInvalidRename(Path dir) throws IOException {
        Files.write(invalid, B);
        assertTrue(repairer.repair(new InvalidFile(invalid, valid, null)).toList().isEmpty());
        assertFalse(Files.exists(invalid));
        Path renamed = dir.resolve("test.png" + FILE_DEL_EXT);
        assertTrue(Files.exists(renamed));
        assertArrayEquals(B, Files.readAllBytes(renamed));
    }

    @Test
    void repairValidNotExistInvalidDelete(Path dir) throws IOException {
        Files.write(invalid, B);
        assertTrue(new DefaultRepairer(true).repair(new InvalidFile(invalid, valid, null)).toList().isEmpty());
        assertFalse(Files.exists(invalid));
        assertFalse(Files.exists(dir.resolve("test.png" + FILE_DEL_EXT)));
    }

    @Test
    void repairBothNotExists() {
        List<InvalidFile> files = repairer.repair(new InvalidFile(invalid, valid, null)).toList();
        assertSame(1, files.size());
        InvalidFile file = files.iterator().next();
        assertSame(invalid, file.invalid());
        assertSame(valid, file.valid());
        assertNull(file.bytes());
    }

    @Test
    void deleteInvalidFileIfItShouldNotExist() {
        assertTrue(new DefaultRepairer(true).deleteInvalidFileIfItShouldNotExist());
    }
}