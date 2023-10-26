package com.webnobis.truebackup.verify.bytes;

import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.model.FileByte;
import com.webnobis.truebackup.model.InvalidByte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultByteVerifierTest {

    private static final Bundle<Path> FILES = new Bundle<>(Path.of("/a/b/test.txt"), Path.of("/x/b/test.txt"));

    private static final Bundle<FileByte> BYTES_NOT_EQUAL = new Bundle<>(new FileByte(FILES.master(), (byte) -1), new FileByte(FILES.copy(), (byte) 99));

    private static final InvalidByte INVALID_BYTE_NOT_EQUAL = new InvalidByte(BYTES_NOT_EQUAL.copy(), BYTES_NOT_EQUAL.master(), 1L, true);

    private static final Bundle<FileByte> BYTES_EQUAL = new Bundle<>(new FileByte(FILES.master(), (byte) 42), new FileByte(FILES.copy(), (byte) 42));

    private static final Bundle<FileByte> BYTES_COPY_END = new Bundle<>(new FileByte(FILES.master(), (byte) 13), new FileByte(FILES.copy(), null));

    private static final InvalidByte INVALID_BYTE_COPY_END = new InvalidByte(BYTES_COPY_END.copy(), BYTES_COPY_END.master(), 1L, true);

    private ByteVerifier<Bundle<FileByte>> verifier;

    @BeforeEach
    void setUp() {
        verifier = new DefaultByteVerifier();
    }

    @Test
    void verifyEqual() {
        List<InvalidByte> list = verifier.verify(BYTES_EQUAL).toList();
        assertTrue(list.isEmpty());
    }

    @Test
    void verifyNotEqual() {
        List<InvalidByte> list = verifier.verify(BYTES_NOT_EQUAL).toList();
        assertSame(1, list.size());
        assertEquals(INVALID_BYTE_NOT_EQUAL, list.iterator().next());
    }

    @Test
    void verifyCopyEnd() {
        List<InvalidByte> list = verifier.verify(BYTES_COPY_END).toList();
        assertSame(1, list.size());
        assertEquals(INVALID_BYTE_COPY_END, list.iterator().next());
    }

    @Test
    void verifyNull() {
        assertTrue(verifier.verify(null).toList().isEmpty());
    }
}