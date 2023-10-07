package com.webnobis.truebackup.verify;

import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.model.FileByte;
import com.webnobis.truebackup.model.InvalidByte;
import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.read.bytes.BytesReader;
import com.webnobis.truebackup.read.bytes.DefaultBytesReader;
import com.webnobis.truebackup.verify.bytes.ByteVerifier;
import com.webnobis.truebackup.verify.bytes.DefaultByteVerifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DefaultVerifierTest {

    private static final Bundle<Path> FILES = new Bundle<>(Path.of("/a/b/test.txt"), Path.of("/x/b/test.txt"));

    private static final Bundle<FileByte> BYTES = new Bundle<>(new FileByte(FILES.master(), (byte) -1), new FileByte(FILES.copy(), (byte) 99));

    private static final InvalidByte INVALID_BYTE = new InvalidByte(BYTES.copy(), BYTES.master(), 7, true);

    private static final InvalidFile INVALID_FILE = new InvalidFile(FILES.copy(), FILES.master(), List.of(INVALID_BYTE));

    private static BytesReader<Bundle<FileByte>, Bundle<Path>> bytesReader;

    private static ByteVerifier<Bundle<FileByte>> byteVerifier;

    private Verifier<Bundle<Path>> verifier;

    @BeforeAll
    static void setUpAll() {
        bytesReader = files -> {
            assertSame(FILES, files);
            return Stream.of(BYTES);
        };
        byteVerifier = bytes -> {
            assertSame(BYTES, bytes);
            return Stream.of(INVALID_BYTE);
        };
    }

    @BeforeEach
    void setUp() {
        verifier = new DefaultVerifier(bytesReader, byteVerifier);
    }

    @Test
    void verify() {
        List<InvalidFile> list = verifier.verify(FILES).toList();
        assertSame(1, list.size());
        assertEquals(INVALID_FILE, list.iterator().next());
    }

    @Test
    void verifyEmpty() {
        List<InvalidFile> list = new DefaultVerifier(bytesReader, unused -> Stream.empty()).verify(FILES).toList();
        assertTrue(list.isEmpty());
    }

    @Test
    void verifyNull() {
        List<?> list = verifier.verify(null).toList();
        assertTrue(list.isEmpty());
    }

    @Test
    void bytesReader() {
        assertEquals(DefaultBytesReader.class, new DefaultVerifier().bytesReader().getClass());
    }

    @Test
    void byteVerifier() {
        assertEquals(DefaultByteVerifier.class, new DefaultVerifier().byteVerifier().getClass());
    }
}