package com.webnobis.truebackup.model;

import java.nio.file.Path;

public record FileByte(Path file, Byte fileByte, long position) {
}
