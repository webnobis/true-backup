package com.webnobis.truebackup.verify;

import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.model.FileByte;
import com.webnobis.truebackup.read.bytes.BytesReader;
import com.webnobis.truebackup.read.bytes.DefaultBytesReader;
import com.webnobis.truebackup.verify.bytes.ByteVerifier;
import com.webnobis.truebackup.verify.bytes.DefaultByteVerifier;

import java.nio.file.Path;

/**
 * Default files verifier<br>
 * If master and copy byte not equals, the copy byte is always voted as invalid, each position
 *
 * @author Steffen Nobis
 */
public class DefaultVerifier extends AbstractVerifier<Bundle<FileByte>, Bundle<Path>> implements Verifier<Bundle<Path>> {

    /**
     * Constructor of defaults
     *
     * @see DefaultBytesReader
     * @see DefaultByteVerifier
     */
    public DefaultVerifier() {
        this(new DefaultBytesReader(), new DefaultByteVerifier());
    }

    DefaultVerifier(BytesReader<Bundle<FileByte>, Bundle<Path>> bytesReader, ByteVerifier<Bundle<FileByte>> byteVerifier) {
        super(bytesReader, byteVerifier);
    }
}
