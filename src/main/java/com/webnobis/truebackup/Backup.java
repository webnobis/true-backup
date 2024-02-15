package com.webnobis.truebackup;


import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.progress.Progress;
import com.webnobis.truebackup.progress.ProgressLog;
import com.webnobis.truebackup.read.Reader;
import com.webnobis.truebackup.repair.Repairer;
import com.webnobis.truebackup.verify.Verifier;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public interface Backup<T> {

    List<Path> dirs();

    Reader<Path> reader();

    Verifier<InvalidFile<T>, Path> verifier();

    Repairer<InvalidFile<T>> repairer();

    default boolean backup() {
        Progress<T> progress = new ProgressLog<>();
        return Objects.requireNonNull(reader(), "reader is null").read(Objects.requireNonNull(dirs(), "dirs is null")).map(progress::read)
                .parallel().map(Objects.requireNonNull(verifier(), "verifier is null")::verify).flatMap(progress::verified).map(progress::repair)
                .map(Objects.requireNonNull(repairer(), "repairer is null")::repair).flatMap(progress::repaired).findAny().isEmpty();
    }

}
