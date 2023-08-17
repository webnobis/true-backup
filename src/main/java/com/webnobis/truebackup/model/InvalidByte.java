package com.webnobis.truebackup.model;

import java.nio.file.Path;
import java.util.Objects;

public record InvalidByte(Path file, long position, Integer read, Integer valid, boolean votingSuccess) {

    static InvalidByte of(ReadByte readByte, Integer valid, boolean votingSuccess) {
        return new InvalidByte(Objects.requireNonNull(readByte).file(), readByte.position(), readByte.read(), valid, votingSuccess);
    }

}
