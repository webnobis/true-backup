package com.webnobis.truebackup.read.bytes;

import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.model.FileByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Default bytes reader
 *
 * @author Steffen Nobis
 */
public class DefaultBytesReader implements BytesReader<Bundle<FileByte>, Bundle<Path>> {

    private static final Logger log = LoggerFactory.getLogger(DefaultBytesReader.class);

    private static synchronized Bundle<FileByte> read(Bundle<Path> files, ByteReader masterReader, ByteReader copyReader) {
        try {
            return new Bundle<>(new FileByte(files.master(), masterReader.readNext()), new FileByte(files.copy(), copyReader.readNext()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Stream<Bundle<FileByte>> read(Bundle<Path> files) {
        if (files == null) {
            return Stream.empty();
        }

        log.debug("read bytes of bundle {}", files);
        ByteReader masterReader = new DefaultByteReader(files.master());
        ByteReader copyReader = new DefaultByteReader(files.copy());
        return Stream.iterate(read(files, masterReader, copyReader), bundle -> Stream.of(bundle.master(), bundle.copy()).map(FileByte::b).anyMatch(Objects::nonNull), unused ->
                read(files, masterReader, copyReader));
    }

}
