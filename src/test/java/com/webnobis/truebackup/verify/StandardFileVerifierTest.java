package com.webnobis.truebackup.verify;

import com.webnobis.truebackup.TempDirExtension;
import com.webnobis.truebackup.model.InvalidFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(TempDirExtension.class)
class StandardFileVerifierTest {

    private static final byte[] BYTES = {1, Byte.MIN_VALUE, 42, Byte.MAX_VALUE};

    private static final byte[] BYTES_SHORTER = {1, Byte.MIN_VALUE, 42};

    private static final byte[] BYTES_OTHER = {-99, Byte.MIN_VALUE, 24, Byte.MAX_VALUE};

    private Path file1;

    private Path file2;

    private Verifier<InvalidFile<Long>, Path> verifier;

    @BeforeEach
    void setUp(Path tmpDir) throws IOException {
        file1 = tmpDir.resolve("file1.txt");
        Files.write(file1, BYTES);
        file2 = tmpDir.resolve("file2.txt");
        verifier = new StandardFileVerifier();
    }

    @Test
    void verify() {
        assertSame(0L, verifier.verify(Collections.emptyList()).count());
        assertSame(0L, verifier.verify(Collections.singletonList(file1)).count());
    }

    @Test
    void verifyEquals() throws IOException {
        Files.write(file2, BYTES);

        assertSame(0L, verifier.verify(List.of(file1, file2)).count());
    }

    @Test
    void verifyNotEquals() throws IOException {
        Files.write(file2, BYTES_OTHER);

        List<InvalidFile<Long>> list = verifier.verify(List.of(file1, file2, Path.of("will/be/ignored.txt"))).toList();
        assertSame(1, list.size());
        InvalidFile<Long> invalidFile = list.get(0);
        assertEquals(file1, invalidFile.validFile());
        assertEquals(file2, invalidFile.invalidFile());
        assertSame(2L, invalidFile.invalidBytes());
    }

    @Test
    void verifyNotEqualsShorter() throws IOException {
        Files.write(file2, BYTES_SHORTER);

        List<InvalidFile<Long>> list = verifier.verify(List.of(file1, file2)).toList();
        assertSame(1, list.size());
        InvalidFile<Long> invalidFile = list.get(0);
        assertEquals(file1, invalidFile.validFile());
        assertEquals(file2, invalidFile.invalidFile());
        assertSame(1L, invalidFile.invalidBytes());

        assertSame(1L, verifier.verify(List.of(file2, file1)).map(InvalidFile::invalidBytes).findAny().orElseThrow());
    }

}