package com.webnobis.truebackup.model;

import java.nio.file.Path;
import java.util.Objects;

/**
 * A file byte
 *
 * @param file the file
 * @param b    the byte if exists, otherwise null
 * @author Steffen Nobis
 */
public record FileByte(Path file, Byte b) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileByte fileByte = (FileByte) o;
        return Objects.equals(file, fileByte.file) && Objects.equals(b, fileByte.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, b);
    }

    @Override
    public String toString() {
        return "FileByte{" +
                "file=" + file +
                ", b=" + b +
                '}';
    }
}
