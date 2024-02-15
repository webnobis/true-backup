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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirExtension.class)
class AbstractFileRepairerTest {

    private static final byte[] BYTES = {1, Byte.MIN_VALUE};

    private static final byte[] BYTES_OTHER = {-99, Byte.MAX_VALUE};

    private Path file1;

    private Path file2;

    private Path archiveDir;

    private List<Path> repair;

    private List<Path> verify;

    private AbstractFileRepairer<Void> repairer;

    @BeforeEach
    void setUp(Path tmpDir) throws IOException {
        file1 = tmpDir.resolve("file1.txt");
        Files.write(file1, BYTES);
        file2 = tmpDir.resolve("file2.txt");
        archiveDir = Files.createTempDirectory(tmpDir, "archive");
        repair = new ArrayList<>(2);
        verify = new ArrayList<>(2);
        repairer = new AbstractFileRepairer<>(archiveDir, list -> {
            verify.addAll(list);
            return Stream.empty();
        }) {

            @Override
            protected void repair(Path invalidFile, Path validFile, Void invalidBytes) {
                repair.add(invalidFile);
                repair.add(validFile);
            }
        };
    }

    @Test
    void copyNew() throws IOException {
        assertFalse(Files.exists(file2));

        AbstractFileRepairer.copy(file1, file2);
        assertArrayEquals(BYTES, Files.readAllBytes(file2));
    }

    @Test
    void copyOverwrite() throws IOException {
        Files.write(file2, BYTES_OTHER);

        AbstractFileRepairer.copy(file1, file2);
        assertArrayEquals(BYTES, Files.readAllBytes(file2));
    }

    @Test
    void copyNotExists() {
        assertThrows(FileNotFoundException.class, () -> AbstractFileRepairer.copy(Path.of("not/exists.dat"), file1));
    }

    @Test
    void repair() throws IOException {
        Files.write(file2, BYTES_OTHER);

        assertSame(0L, repairer.repair(new InvalidFile<>(file2, file1, null)).count());
        assertEquals(repair, verify);
        Path archiveFile = archiveDir.resolve(file2.getFileName());
        assertTrue(Files.exists(archiveFile));
        assertArrayEquals(BYTES_OTHER, Files.readAllBytes(archiveFile));
        assertArrayEquals(BYTES_OTHER, Files.readAllBytes(file2));
    }

    @Test
    void repairCreate() throws IOException {
        assertFalse(Files.exists(file2));

        assertSame(0L, repairer.repair(new InvalidFile<>(file2, file1, null)).count());
        assertTrue(Files.exists(file2));

        assertTrue(repair.isEmpty());
        assertEquals(List.of(file2, file1), verify);
        assertFalse(Files.exists(archiveDir.resolve(file2.getFileName())));
        assertArrayEquals(BYTES, Files.readAllBytes(file2));
    }

    @Test
    void repairMove() throws IOException {
        assertFalse(Files.exists(file2));

        assertSame(0L, repairer.repair(new InvalidFile<>(file1, file2, null)).count());
        assertFalse(Files.exists(file2));

        assertTrue(repair.isEmpty());
        assertEquals(List.of(file1, file2), verify);
        Path archiveFile = archiveDir.resolve(file1.getFileName());
        assertTrue(Files.exists(archiveFile));
        assertArrayEquals(BYTES, Files.readAllBytes(archiveFile));
    }

    @Test
    void moveNew(Path tmpDir) throws IOException {
        assertFalse(Files.exists(file2));

        repairer.move(file1, file2);
        assertArrayEquals(BYTES, Files.readAllBytes(file2));
    }

    @Test
    void moveOverwrite(Path tmpDir) throws IOException {
        Files.write(file2, BYTES_OTHER);

        repairer.move(file1, file2);
        assertArrayEquals(BYTES, Files.readAllBytes(file2));
    }
}