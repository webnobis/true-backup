package com.webnobis.truebackup.model;

import com.webnobis.truebackup.TempDirExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirExtension.class)
class InvalidFileTest {

    private static Path invalid;

    private static Path valid;

    private InvalidFile invalidFile;

    @BeforeAll
    static void setUpAll(Path dir) throws IOException {
        invalid = dir.resolve("test1.txt");
        Files.createFile(invalid);
        valid = dir.resolve("test2.txt");
        Files.createFile(valid);
    }

    @BeforeEach
    void setUp() {
        invalidFile = new InvalidFile(invalid, valid, List.of(new InvalidByte(new FileByte(invalid, (byte) 42), new FileByte(valid, (byte) -1), 0L, true)));
    }

    @Test
    void shouldBeCreated(Path dir) {
        assertFalse(invalidFile.shouldBeCreated());
        assertFalse(new InvalidFile(null, null, null).shouldBeCreated());
        assertFalse(new InvalidFile(null, dir.resolve("not-exists.txt"), null).shouldBeCreated());
        assertFalse(new InvalidFile(null, valid, null).shouldBeCreated());

        assertTrue(new InvalidFile(dir.resolve("not-exists.txt"), valid, null).shouldBeCreated());
    }

    @Test
    void shouldBeDeleted(Path dir) {
        assertFalse(invalidFile.shouldBeDeleted());
        assertFalse(new InvalidFile(null, null, null).shouldBeDeleted());
        assertFalse(new InvalidFile(dir.resolve("not-exists.txt"), null, null).shouldBeDeleted());
        assertFalse(new InvalidFile(invalid, null, null).shouldBeDeleted());

        assertTrue(new InvalidFile(invalid, dir.resolve("not-exists.txt"), null).shouldBeDeleted());
    }

    @Test
    void votingSuccess() {
        assertTrue(invalidFile.votingSuccess());
        assertTrue(new InvalidFile(invalid, valid, null).votingSuccess());
    }

    @Test
    void invalid() {
        assertSame(invalid, invalidFile.invalid());
    }

    @Test
    void valid() {
        assertSame(valid, invalidFile.valid());
    }

    @Test
    void bytes() {
        assertSame(1, invalidFile.bytes().size());
        assertNull(new InvalidFile(invalid, valid, null).bytes());
    }

    @Test
    void equals() {
        assertEquals(invalidFile, new InvalidFile(invalid, Path.of(valid.toString()), invalidFile.bytes()));
    }
}