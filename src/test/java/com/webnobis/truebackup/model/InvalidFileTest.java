package com.webnobis.truebackup.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvalidFileTest {

    private static final Path INVALID = Path.of("a/b/c/test1.txt");

    private static final Path VALID = Path.of("1/2/test2.txt");

    private InvalidFile file;

    @BeforeEach
    void setUp() {
        file = new InvalidFile(INVALID, VALID, List.of(new InvalidByte(new FileByte(INVALID, (byte) 42), new FileByte(VALID, (byte) -1), 0L, true)));
    }

    @Test
    void votingSuccess() {
        assertTrue(file.votingSuccess());
        assertTrue(new InvalidFile(INVALID, VALID, null).votingSuccess());
    }

    @Test
    void invalid() {
        assertSame(INVALID, file.invalid());
    }

    @Test
    void valid() {
        assertSame(VALID, file.valid());
    }

    @Test
    void bytes() {
        assertSame(1, file.bytes().size());
        assertNull(new InvalidFile(INVALID, VALID, null).bytes());
    }
}