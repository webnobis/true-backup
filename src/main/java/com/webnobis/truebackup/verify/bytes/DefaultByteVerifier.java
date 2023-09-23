package com.webnobis.truebackup.verify.bytes;

import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.model.FileByte;
import com.webnobis.truebackup.model.InvalidByte;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Default bytes verifier<br>
 * If master and copy byte not equals, the copy byte is always voted as invalid
 *
 * @author Steffen Nobis
 */
public class DefaultByteVerifier extends AbstractByteVerifier<Bundle<FileByte>> {

    @Override
    public Stream<InvalidByte> verify(Bundle<FileByte> fileBytes) {
        if (fileBytes == null) {
            return Stream.empty();
        }

        long position = super.nextPosition();
        if (Objects.equals(fileBytes.master().b(), fileBytes.copy().b())) {
            return Stream.empty();
        } else {
            return Stream.of(new InvalidByte(fileBytes.copy(), fileBytes.master(), position, true));
        }
    }
}
