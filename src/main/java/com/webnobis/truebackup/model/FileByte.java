package com.webnobis.truebackup.model;

import java.nio.file.Path;

/**
 * A file byte
 *
 * @param file the file
 * @param b    the byte if exists, otherwise null
 * @author Steffen Nobis
 */
public record FileByte(Path file, Byte b) {
    @Override
    public String toString() {
        return "FileByte{" +
                "file=" + file +
                ", b=" + b +
                '}';
    }
}
