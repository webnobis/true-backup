package com.webnobis.truebackup.repair;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileRelativizerTest {

    @Test
    void relativize() {
        Path expected = Path.of("x/y/the-file.txt");
        Path file1 = Path.of("a/b/c/x/y/the-file.txt");
        Path file2 = Path.of("1/2/3/4/5/x/y/the-file.txt");
        assertEquals(expected.toString(), FileRelativizer.relativize(file1, file2).toString());
    }

    @Test
    void relativizeNotEqualFileNames() {
        Path file1 = Path.of("dir/file1.txt");
        Path file2 = Path.of("dir/file2.txt");
        assertEquals("", FileRelativizer.relativize(file1, file2).toString());
    }

    @Test
    void relativizeNull() {
        assertThrows(NullPointerException.class, () -> FileRelativizer.relativize(Path.of(""), null));
        assertThrows(NullPointerException.class, () -> FileRelativizer.relativize(null, Path.of("")));
    }
}