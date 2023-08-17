package com.webnobis.truebackup.verify;

import com.webnobis.truebackup.model.InvalidByte;
import com.webnobis.truebackup.model.InvalidFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public record SimpleFileVerify(Path file, Path copy) implements Verify<InvalidFile> {

    @Override
    public Stream<InvalidFile> verify() {
        List<InvalidByte> invalidBytes = new SimpleByteVerify(file, copy).verify().toList();
        return invalidBytes.isEmpty() ? Stream.empty() : Stream.of(new InvalidFile(invalidBytes.iterator().next().file(), invalidBytes));
    }
}

