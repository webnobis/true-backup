package com.webnobis.truebackup.read;

import com.webnobis.truebackup.TempDirExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirExtension.class)
class ByteIteratorTest {

    private static final byte[] BYTES = {-1, 0, Byte.MIN_VALUE, Byte.MAX_VALUE};

    private static final byte[] BYTES_1GB = IntStream.range(0, 10000000).mapToObj(String::valueOf)
            .limit(1024 * 1024 * 1024)
            .collect(Collectors.joining()).getBytes();

    private Iterator<Byte> it;

    private Iterator<?> itNoFile;

    @BeforeEach
    void setUp(Path tmpDir) throws IOException {
        Path tmpFile = Files.createTempFile(tmpDir, ByteIteratorTest.class.getSimpleName(), ".txt");
        Files.write(tmpFile, BYTES);
        it = new ByteIterator(tmpFile);
        itNoFile = new ByteIterator(Path.of("not-existing-file.txt"));
    }

    @Test
    void hasNext() {
        assertTrue(it.hasNext());
        Stream.generate(it::next).limit(BYTES.length - 1).count();
        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());

        assertFalse(itNoFile.hasNext());
    }

    @Test
    void next() {
        int[] expected = IntStream.range(0, BYTES.length).map(i -> BYTES[i]).toArray();
        int[] read = Stream.generate(it::next).limit(BYTES.length).mapToInt(Byte::intValue).toArray();
        assertArrayEquals(expected, read);
        assertNull(it.next());

        assertNull(itNoFile.next());
    }

    @Test
    void nextBig(Path tmpDir) throws IOException {
        Path file = Files.createFile(tmpDir.resolve("big.1gb"));
        Files.write(file, BYTES_1GB);
        Iterator<Byte> itBig = new ByteIterator(file);
        IntStream.range(0, BYTES_1GB.length)
                .forEach(i -> assertEquals(BYTES_1GB[i], itBig.next()));
    }

}