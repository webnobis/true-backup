package com.webnobis.truebackup.verify.bytes;

import com.webnobis.truebackup.model.InvalidByte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AbstractByteVerifierTest {

    AbstractByteVerifier<Void> verifier;

    @BeforeEach
    void setUp() {
        verifier = new AbstractByteVerifier<>() {
            @Override
            public Stream<InvalidByte> verify(Void fileBytes) {
                return Stream.empty();
            }
        };
        assertSame(0L, verifier.positionRef.get());
    }

    @Test
    void nextPosition() {
        LongStream.rangeClosed(1, 5).peek(unused -> verifier.nextPosition()).forEach(l -> assertSame(l, verifier.positionRef.get()));
    }

    @Test
    void resetPosition() {
        verifier.nextPosition();
        assertNotEquals(0L, verifier.positionRef.get());

        verifier.resetPosition();
        assertSame(0L, verifier.positionRef.get());
    }
}