package com.webnobis.truebackup.verify;


import com.webnobis.truebackup.model.FileByte;
import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.read.ByteReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class StandardFileVerifier implements Verifier<InvalidFile<Long>, Path>, ByteReader {

    private static final Logger log = LoggerFactory.getLogger(StandardFileVerifier.class);

    private static boolean equals(List<FileByte> bytes) {
        Byte first = bytes.get(0).fileByte();
        return bytes.stream().map(FileByte::fileByte).allMatch(b -> Objects.equals(first, b));
    }

    @Override
    public Stream<InvalidFile<Long>> verify(List<Path> bundle) {
        if (Objects.requireNonNull(bundle, "bundle is null").size() < 2) {
            // ever valid, if empty or once
            return Stream.empty();
        }
        if (bundle.size() > 2) {
            log.warn("Standard backup only supports bundles with up to 2 files, but found bundle with {} files. The others were ignored.", bundle.size());
        }

        long invalidBytes = read(bundle.subList(0, 2)).map(StandardFileVerifier::equals).filter(Boolean.FALSE::equals).count();
        if (invalidBytes > 0) {
            Path valid = bundle.get(0);
            Path invalid = bundle.get(1);
            log.info("Found {} invalid bytes in bundle. Creates invalid file of valid master {} and invalid copy {}.", invalidBytes, valid, invalid);
            return Stream.of(new InvalidFile<>(invalid, valid, invalidBytes));
        } else {
            return Stream.empty();
        }
    }

}
