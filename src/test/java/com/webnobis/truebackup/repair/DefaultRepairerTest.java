package com.webnobis.truebackup.repair;

import com.webnobis.truebackup.TempDirExtension;
import com.webnobis.truebackup.model.InvalidFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TempDirExtension.class)
class DefaultRepairerTest {

    private static final byte[] B = {-1, 9, 42, 127, -99};

    private Path invalid;

    private Path valid;

    private InvalidFile invalidFile;

    private AbstractRepairer repairer;

    @BeforeEach
    void setUp(Path dir) throws IOException {
        invalid = dir.resolve("test.png");
        valid = dir.resolve("other.jpg");
        Files.write(valid, B);
        Files.write(invalid, new byte[]{6});
        invalidFile = new InvalidFile(invalid, valid, null);
        repairer = new DefaultRepairer(false);
    }

    @Test
    void repairBytes() throws IOException {
        repairer.repairBytes(invalidFile);
        assertArrayEquals(B, Files.readAllBytes(invalid));
    }

    @Test
    void repairBytesNull() {
        assertThrows(NullPointerException.class, () -> repairer.repairBytes(null));
        assertThrows(NullPointerException.class, () -> repairer.repairBytes(new InvalidFile(null, null, null)));
    }
}