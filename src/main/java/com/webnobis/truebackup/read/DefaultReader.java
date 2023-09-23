package com.webnobis.truebackup.read;

import com.webnobis.truebackup.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Default files reader
 *
 * @author Steffen Nobis
 */
public class DefaultReader implements Reader<Bundle<Path>> {

    private static final Logger log = LoggerFactory.getLogger(DefaultReader.class);

    @Override
    public Stream<Bundle<Path>> read(Bundle<Path> dirs) {
        if (dirs == null) {
            return Stream.empty();
        }

        try {
            return Stream.concat(Reader.relativizeFiles(dirs.master()), Reader.relativizeFiles(dirs.copy()))
                    .distinct().map(file -> new Bundle<>(dirs.master().resolve(file), dirs.copy().resolve(file)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new UncheckedIOException(e);
        }
    }

}
