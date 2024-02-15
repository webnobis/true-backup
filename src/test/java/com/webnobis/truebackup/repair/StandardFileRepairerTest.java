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
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(TempDirExtension.class)
class StandardFileRepairerTest {

    private static final byte[] BYTES = {-1, 42};

    private static final byte[] BYTES_OTHER = {-99, Byte.MAX_VALUE};

    private Repairer<InvalidFile<Long>> repairer;

    private Path file1;

    private Path file2;

    @BeforeEach
    void setUp(Path tmpDir) throws IOException {
        file1 = tmpDir.resolve("file1.txt");
        Files.write(file1, BYTES);
        file2 = tmpDir.resolve("file2.txt");
        Files.write(file2, BYTES_OTHER);
        repairer = new StandardFileRepairer(Files.createTempDirectory(tmpDir, "archive"));
    }

    @Test
    void repair1() throws IOException {
        assertSame(0L, repairer.repair(new InvalidFile<>(file1, file2, 7L)).count());
        assertArrayEquals(BYTES_OTHER, Files.readAllBytes(file1));
    }

    @Test
    void repair2() throws IOException {
        assertSame(0L, repairer.repair(new InvalidFile<>(file2, file1, 7L)).count());
        assertArrayEquals(BYTES, Files.readAllBytes(file2));
    }
}