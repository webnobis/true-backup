package com.webnobis.truebackup.verify.bytes;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract bytes verifier
 *
 * @param <T> the bundle type
 * @author Steffen Nobis
 */
public abstract class AbstractByteVerifier<T> implements ByteVerifier<T> {

    private final AtomicLong positionRef = new AtomicLong();

    /**
     * Increments the position
     *
     * @return next position
     */
    protected long nextPosition() {
        return positionRef.incrementAndGet();
    }

    @Override
    public void resetPosition() {
        positionRef.set(0);
    }
}
