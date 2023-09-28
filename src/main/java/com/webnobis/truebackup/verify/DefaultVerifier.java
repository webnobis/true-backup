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
 * @author Steffen Nobis
 * @see DefaultByteVerifier#verify(Bundle)
 */
public class DefaultVerifier implements Verifier<Bundle<Path>> {

    private final BytesReader<Bundle<FileByte>, Bundle<Path>> bytesReader = new DefaultBytesReader();

    private final ByteVerifier<Bundle<FileByte>> byteVerifier = new DefaultByteVerifier();

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
