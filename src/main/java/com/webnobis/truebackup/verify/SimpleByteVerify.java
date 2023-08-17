package com.webnobis.truebackup.verify;

import com.webnobis.truebackup.model.InvalidByte;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public record SimpleByteVerify(Path file, Path copy) implements Verify<InvalidByte> {

    private static InvalidByte toInvalidByte(Path file, long position, int read, int valid) {
        return read == valid ? null : new InvalidByte(file, position, toInteger(read), toInteger(valid), true);
    }

    private static Integer toInteger(int read) {
        return read < 0 ? null : read;
    }

    @Override
    public Stream<InvalidByte> verify() {
        try (BufferedInputStream inFile = new BufferedInputStream(Files.newInputStream(Objects.requireNonNull(file))); BufferedInputStream inCopy = new BufferedInputStream(Files.newInputStream(Objects.requireNonNull(copy)))) {
            long maxLength = Math.max(Files.size(file), Files.size(copy));
            return LongStream.range(0, maxLength).mapToObj(position -> {
                try {
                    return toInvalidByte(copy, position, inCopy.read(), inFile.read());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }).filter(Objects::nonNull);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
