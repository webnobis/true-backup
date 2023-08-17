package com.webnobis.truebackup.model;

import java.nio.file.Path;

public record ReadByte(Path file, long position, Integer read) {
}
