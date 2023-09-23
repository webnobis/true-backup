package com.webnobis.truebackup.read;

import com.webnobis.truebackup.TempDirExtension;
import com.webnobis.truebackup.model.Bundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirExtension.class)
class DefaultReaderTest {

    private static byte[] B = {-1, 42, Byte.MIN_VALUE};

    private Path file1, file2, file3;

    private Path dir1, dir2;

    private Reader<Bundle<Path>> reader;

    @BeforeEach
    void setUp(Path dir) throws IOException {
        dir1 = dir.resolve("111/222");
        dir2 = dir.resolve("333");
        file1 = dir1.resolve("a/b/c/d/test1.txt");
        Files.createDirectories(file1.getParent());
        Files.write(file1, new byte[]{1, 2, 3, 4});
        file2 = dir1.resolve("x/y/test2.txt");
        Files.createDirectories(file2.getParent());
        Files.write(file2, new byte[]{-1, 42});
        file3 = dir2.resolve("1/2/3/test3.txt");
        Files.createDirectories(file3.getParent());
        Files.write(file3, new byte[]{Byte.MIN_VALUE});

        reader = new DefaultReader();
    }

    @Test
    void read() {
        List<Bundle<Path>> bundles = reader.read(new Bundle<>(dir1, dir2)).toList();
        assertSame(3, bundles.size());

        Bundle<Path> b1 = bundles.stream().filter(b -> file1.equals(b.master())).findAny().orElseThrow();
        assertTrue(Files.exists(b1.master()));
        assertEquals(dir1.relativize(file1).toString(), dir2.relativize(b1.copy()).toString());
        Bundle<Path> b2 = bundles.stream().filter(b -> file2.equals(b.master())).findAny().orElseThrow();
        assertTrue(Files.exists(b2.master()));
        assertEquals(dir1.relativize(file2).toString(), dir2.relativize(b2.copy()).toString());

        Bundle<Path> b3 = bundles.stream().filter(b -> file3.equals(b.copy())).findAny().orElseThrow();
        assertTrue(Files.exists(b3.copy()));
        assertEquals(dir2.relativize(file3).toString(), dir1.relativize(b3.master()).toString());
    }

    @Test
    void readNull() {
        assertTrue(reader.read(null).toList().isEmpty());
    }
}