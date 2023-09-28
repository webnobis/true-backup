package com.webnobis.truebackup.read.bytes;

import com.webnobis.truebackup.TempDirExtension;
import com.webnobis.truebackup.model.Bundle;
import com.webnobis.truebackup.model.FileByte;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(TempDirExtension.class)
class DefaultBytesReaderTest {

    private static final byte[] B = {-1, 42};

    private Path master;

    private Path copy;

    private BytesReader<Bundle<FileByte>, Bundle<Path>> reader;

    @BeforeEach
    void setUp(Path dir) throws IOException {
        master = dir.resolve("master.dat");
        Files.write(master, B);
        copy = dir.resolve("copy.www");
        Files.copy(master, copy);
        reader = new DefaultBytesReader();
    }

    @Test
    void read() {
        Map<Byte, Byte> map = reader.read(new Bundle<>(master, copy))
                .collect(Collectors.toMap(b -> b.master().b(), b -> b.copy().b()));
        assertSame(B.length, map.size());
        map.forEach(Assertions::assertEquals);
    }

    @Test
    void readNull() {
        assertSame(0L, reader.read(new Bundle<>(null, null)).count());
        assertSame(0L, reader.read(null).count());
    }
}