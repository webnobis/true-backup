package com.webnobis.truebackup.model;

import java.nio.file.Files;
import java.nio.file.Path;

public record InvalidFile<T>(Path invalidFile, Path validFile, T invalidBytes) {

    public boolean shouldCreate() {
        return Files.exists(validFile) && !Files.exists(invalidFile);
    }

    public boolean shouldRepair() {
        return Files.exists(validFile) && Files.exists(invalidFile);
    }

}
