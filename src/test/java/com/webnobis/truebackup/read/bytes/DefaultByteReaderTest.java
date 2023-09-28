package com.webnobis.truebackup.read.bytes;

import com.webnobis.truebackup.TempDirExtension;
import com.webnobis.truebackup.model.Bundle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirExtension.class)
class DefaultByteReaderTest {

    @Test
    void readNext(Path dir) throws IOException {
        byte[] expected = {-1, 42, -99, Byte.MAX_VALUE};
        Path file = dir.resolve("small.txt");
        Files.write(file, expected);

        ByteReader byteReader = new DefaultByteReader(file);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Stream.generate(() -> {
                try {
                    return byteReader.readNext();
                } catch (IOException e) {
                    fail(e);
                    return null;
                }
            }).limit(expected.length).mapToInt(Byte::byteValue).forEach(out::write);
            out.flush();
            assertArrayEquals(expected, out.toByteArray());
        }
        assertTrue(IntStream.range(0, expected.length).mapToObj(unused -> {
            try {
                return byteReader.readNext();
            } catch (IOException e) {
                fail(e);
                return null;
            }
        }).allMatch(Objects::isNull));
    }

    @Test
    void readNextBig(Path dir) throws IOException {
        int length = 1024 * 1024;
        Path file = dir.resolve("big.txt");
        try (OutputStream out = Files.newOutputStream(file)) {
            IntStream.range(1, length).limit(length).forEach(b -> {
                try {
                    out.write(b);
                } catch (IOException e) {
                    fail(e);
                }
            });
            out.flush();
        }

        ByteReader byteReader = new DefaultByteReader(file);
        try (InputStream in = Files.newInputStream(file)) {
            Stream.generate(() -> {
                try {
                    return new Bundle<>((byte) in.read(), byteReader.readNext());
                } catch (IOException e) {
                    fail(e);
                    return null;
                }
            }).limit(length - 1).forEach(bundle -> assertEquals(bundle.master(), bundle.copy()));
        }
        assertNull(byteReader.readNext());
    }

    @Test
    void readNextEmpty(Path dir) throws IOException {
        Path file = dir.resolve("empty.txt");
        Files.createFile(file);

        ByteReader byteReader = new DefaultByteReader(file);
        assertNull(byteReader.readNext());
        assertNull(byteReader.readNext());
    }

    @Test
    void readNextNotExists(Path dir) throws IOException {
        Path file = dir.resolve("1/2/3/not-exists.txt");

        ByteReader byteReader = new DefaultByteReader(file);
        assertNull(byteReader.readNext());
        assertNull(byteReader.readNext());
    }

    @Test
    void readNextNull() throws IOException {
        ByteReader byteReader = new DefaultByteReader(null);
        assertNull(byteReader.readNext());
        assertNull(byteReader.readNext());
    }

}