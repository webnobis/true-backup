package com.webnobis.truebackup.verify.bytes;

import com.webnobis.truebackup.model.InvalidByte;

import java.util.stream.Stream;

/**
 * Bytes verifier
 *
 * @param <T> the bundle type
 * @author Steffen Nobis
 */
@FunctionalInterface
public interface ByteVerifier<T> {

    /**
     * Stream of all invalid bytes
     *
     * @param fileBytes the bundle of file bytes
     * @return all invalid files
     */
    Stream<InvalidByte> verify(T fileBytes);

    /**
     * Resets the position
     *
     * @see InvalidByte#position()
     */
    default void resetPosition() {
    }

}
