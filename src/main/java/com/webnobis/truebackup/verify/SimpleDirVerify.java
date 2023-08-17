package com.webnobis.truebackup.verify;

import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.model.ReadFile;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record SimpleDirVerify(Path dir, Path copy) implements Verify<InvalidFile> {

    @Override
    public Stream<InvalidFile> verify() {
        Collection<List<Path>> files = Stream.concat(FileStream.read(dir), FileStream.read(copy))
                .collect(Collectors.groupingByConcurrent(ReadFile::relativePath, Collectors.mapping(ReadFile::file, Collectors.toList()))).values();
        return files.stream().filter(list -> list.size() > 1).map(list -> toVerify(list.get(0), list.get(1)))
                .flatMap(SimpleFileVerify::verify);
    }

    private SimpleFileVerify toVerify(Path file1, Path file2) {
        return file1.toString().startsWith(dir.toString()) ? new SimpleFileVerify(file1, file2) : new SimpleFileVerify(file2, file1);
    }
}

