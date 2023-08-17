package com.webnobis.truebackup.model;

import java.nio.file.Path;
import java.util.Objects;

public record ReadFile(String relativePath, Path file) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(relativePath, ((ReadFile) o).relativePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relativePath);
    }
}
