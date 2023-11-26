package com.webnobis.truebackup.progress;

import com.webnobis.truebackup.model.InvalidFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProgressLogTest {

    private ProgressLog progress;

    @BeforeEach
    void setUp() {
        progress = new ProgressLog();
    }

    @Test
    void read() {
        assertSame(0L, progress.foundRef.get());
        assertSame(0L, progress.workingRef.get());

        LongStream.rangeClosed(1, 10).boxed().map(progress::read).forEach(l -> {
            assertSame(l, progress.foundRef.get());
            assertSame(l, progress.workingRef.get());
        });
    }

    @Test
    void repair() {
        assertSame(0L, progress.foundRef.get());
        assertSame(0L, progress.workingRef.get());

        InvalidFile invalidFile = new InvalidFile(null, null, null);
        assertEquals(invalidFile, progress.repair(invalidFile));
        assertSame(0L, progress.foundRef.get());
        assertSame(1L, progress.workingRef.get());
    }

    @Test
    void verified() {
        assertTrue(progress.read(true));
        assertSame(1L, progress.workingRef.get());

        Stream.<Stream<InvalidFile>>of(Stream.of(new InvalidFile(null, null, null)), Stream.empty(), null).forEach(stream -> {
            assertSame(stream, progress.verified(stream));
            assertSame(0L, progress.workingRef.get());
        });
    }

    @Test
    void repaired() {
        assertNull(progress.repair(null));
        assertSame(1L, progress.workingRef.get());

        Stream.<Stream<InvalidFile>>of(Stream.of(new InvalidFile(null, null, null)), Stream.empty(), null).forEach(stream -> {
            assertSame(stream, progress.repaired(stream));
            assertSame(0L, progress.workingRef.get());
        });
    }

    @RepeatedTest(10)
    void multiThreaded() {
        long count = IntStream.range(0, 100).boxed().parallel().map(progress::read).map(i -> progress.repaired(i % 2 > 0 ? Stream.of(new InvalidFile(null, null, null)) : Stream.empty()))
                .toList().size();
        assertSame(count, progress.foundRef.get());
        assertSame(0L, progress.workingRef.get());
    }
}