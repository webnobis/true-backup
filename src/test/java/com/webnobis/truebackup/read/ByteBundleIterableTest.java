package com.webnobis.truebackup.read;

import com.webnobis.truebackup.TempDirExtension;
import com.webnobis.truebackup.model.FileByte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirExtension.class)
class ByteBundleIterableTest {

    private static final byte[] BYTES1 = {-1, 0, Byte.MIN_VALUE, Byte.MAX_VALUE};

    private static final byte[] BYTES2 = {-1, 0, Byte.MIN_VALUE, Byte.MAX_VALUE, 99, 42};

    private ByteBundleIterable it;

    @BeforeEach
    void setUp(Path tmpDir) {
        it = new ByteBundleIterable(IntStream.rangeClosed(1, 10)
                .mapToObj(i -> {
                    try {
                        Path file = Files.createTempFile(tmpDir, ByteBundleIterableTest.class.getSimpleName() + i, ".dat");
                        Files.write(file, i < 2 ? BYTES1 : BYTES2);
                        return file;
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }).toList());
    }

    @Test
    void hasNext() {
        Stream.generate(it::next).limit(BYTES2.length - 1).forEach(unused -> assertTrue(it.hasNext()));
        it.next();
        assertFalse(it.hasNext());
    }

    @Test
    void next() {
        IntStream.range(0, BYTES1.length).forEach(i -> {
            List<FileByte> list = it.next();
            assertSame(10, list.size());
            assertTrue(list.stream().map(FileByte::fileByte).allMatch(b -> b.equals(BYTES1[i])));
        });
        IntStream.range(BYTES1.length, BYTES2.length).forEach(i -> {
            List<FileByte> list = new ArrayList<>(it.next());
            assertSame(10, list.size());
            assertTrue(list.remove(list.stream().filter(fb -> fb.fileByte() == null).findAny().orElseThrow()));
            assertTrue(list.stream().map(FileByte::fileByte).allMatch(b -> b.equals(BYTES2[i])));
        });
    }

    @Test
    void iterator() {
        assertSame(it, it.iterator());
    }
}