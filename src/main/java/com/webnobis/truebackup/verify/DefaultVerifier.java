package com.webnobis.truebackup.verify;

import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.model.FileByte;
import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.read.bytes.BytesReader;
import com.webnobis.truebackup.read.bytes.DefaultBytesReader;
import com.webnobis.truebackup.verify.bytes.ByteVerifier;
import com.webnobis.truebackup.verify.bytes.DefaultByteVerifier;

import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default files verifier<br>
 * If master and copy byte not equals, the copy byte is always voted as invalid, each position
 *
 * @param bytesReader  the bytes reader
 * @param byteVerifier the bytes verifier
 * @author Steffen Nobis
 */
public record DefaultVerifier(BytesReader<Bundle<FileByte>, Bundle<Path>> bytesReader,
                              ByteVerifier<Bundle<FileByte>> byteVerifier) implements Verifier<Bundle<Path>> {

    /**
     * Constructor of defaults
     *
     * @see DefaultBytesReader
     * @see DefaultByteVerifier
     */
    public DefaultVerifier() {
        this(new DefaultBytesReader(), new DefaultByteVerifier());
    }

    @Override
    public Stream<InvalidFile> verify(Bundle<Path> files) {
        if (files == null) {
            return Stream.empty();
        }

        byteVerifier.resetPosition();
        return bytesReader.read(files).parallel().flatMap(byteVerifier::verify)
                .collect(Collectors.groupingByConcurrent(invalidByte -> new InvalidFile(invalidByte.invalid().file(), invalidByte.valid().file(), null)))
                .entrySet().stream().map(e -> new InvalidFile(e.getKey().invalid(), e.getKey().valid(), e.getValue()));
    }

}
