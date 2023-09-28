package com.webnobis.truebackup.read;

import com.webnobis.truebackup.TempDirExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirExtension.class)
class ReaderTest {

    private static byte[] B = {-1, 42, Byte.MIN_VALUE};

    private static Path RELATIVE_1 = Path.of("a/b/c/d/test1.txt");

    private static Path RELATIVE_2 = Path.of("1/2/test2.txt");

    @BeforeEach
    void setUp(Path dir) throws IOException {
        Path file1 = dir.resolve(RELATIVE_1);
        Files.createDirectories(file1.getParent());
        Files.write(file1, B);
        Path file2 = dir.resolve(RELATIVE_2);
        Files.createDirectories(file2.getParent());
        Files.write(file2, B);
    }

    @Test
    void relativizeFiles(Path dir) throws IOException {
        List<String> paths = Reader.relativizeFiles(dir).map(Path::toString).toList();
        assertSame(2, paths.size());
        assertTrue(paths.contains(RELATIVE_1.toString()));
        assertTrue(paths.contains(RELATIVE_2.toString()));
        paths.stream().map(dir::resolve).forEach(file -> {
            try {
                assertArrayEquals(B, Files.readAllBytes(file));
            } catch (IOException e) {
                fail(e);
            }
        });
    }

    @Test
    void relativizeFilesFile(Path dir) throws IOException {
        Path file = dir.resolve("file.dat");
        Files.createFile(file);

        List<Path> paths = Reader.relativizeFiles(file).toList();
        assertSame(1, paths.size());
        assertSame(file, paths.iterator().next());
    }

    @Test
    void relativizeFilesNull() throws IOException {
        assertTrue(Reader.relativizeFiles(null).toList().isEmpty());
    }

}