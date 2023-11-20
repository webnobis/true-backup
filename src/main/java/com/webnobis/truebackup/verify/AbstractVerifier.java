package com.webnobis.truebackup.verify;

import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.read.bytes.BytesReader;
import com.webnobis.truebackup.verify.bytes.ByteVerifier;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract files verifier
 *
 * @author Steffen Nobis
 */
public class AbstractVerifier<R, T> implements Verifier<T> {

    private final BytesReader<R, T> bytesReader;

    private final ByteVerifier<R> byteVerifier;

    /**
     * Constructor of verifier
     *
     * @param bytesReader  the bytes reader
     * @param byteVerifier the bytes verifier
     */
    protected AbstractVerifier(BytesReader<R, T> bytesReader,
                               ByteVerifier<R> byteVerifier) {
        this.bytesReader = Objects.requireNonNull(bytesReader, "bytes reader is null");
        this.byteVerifier = Objects.requireNonNull(byteVerifier, "byte verifier is null");
    }

    protected BytesReader<R, T> bytesReader() {
        return bytesReader;
    }

    protected ByteVerifier<R> byteVerifier() {
        return byteVerifier;
    }

    @Override
    public Stream<InvalidFile> verify(T files) {
        if (files == null) {
            return Stream.empty();
        }

        byteVerifier.resetPosition();
        return bytesReader.read(files).parallel().flatMap(byteVerifier::verify)
                .collect(Collectors.groupingByConcurrent(invalidByte -> new InvalidFile(invalidByte.invalid().file(), invalidByte.valid().file(), null)))
                .entrySet().stream().map(e -> new InvalidFile(e.getKey().invalid(), e.getKey().valid(), e.getValue()));
    }

}
