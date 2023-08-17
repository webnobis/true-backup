package com.webnobis.truebackup;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class ReadWriteTest {

    public static void main(String[] args) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(Path.of("test.jpg"))); RandomAccessFile file = new RandomAccessFile("test-rewrite.jpg", "rw")) {
            AtomicLong posRef = new AtomicLong();
            IntStream.iterate(in.read(), read -> read > -1, unused -> {
                try {
                    return in.read();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }).forEach(i -> {
                try {
                    file.seek(posRef.getAndIncrement());
                    file.write(i);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }
}
