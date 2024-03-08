package com.webnobis.truebackup;

import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.read.Reader;
import com.webnobis.truebackup.repair.Repairer;
import com.webnobis.truebackup.verify.Verifier;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BackupTest {

    private static final List<Path> DIRS = List.of(Path.of("dir1"), Path.of("dir2"));

    private static final List<Path> FILES = List.of(Path.of("file1"), Path.of("file2"), Path.of("file3"));

    private static final InvalidFile<Void> INVALID_FILE = new InvalidFile<>(null, null, null);

    @Test
    void backup() {
        Stream.of(Boolean.TRUE, Boolean.FALSE).forEach(success -> {
            AtomicInteger count = new AtomicInteger(3);
            Backup<Void> backup = new TestBackup(success, count);
            assertEquals(success, new TestBackup(success, count).backup());
            assertSame(0, count.get());
        });
    }

    private record TestBackup(boolean success, AtomicInteger count) implements Backup<Void> {

        @Override
        public List<Path> dirs() {
            return DIRS;
        }

        @Override
        public Reader<Path> reader() {
            return list -> {
                assertSame(DIRS, list);
                count.decrementAndGet();
                return Stream.of(FILES);
            };
        }

        @Override
        public Verifier<InvalidFile<Void>, Path> verifier() {
            return list -> {
                assertSame(FILES, list);
                count.decrementAndGet();
                return Stream.of(INVALID_FILE);
            };
        }

        @Override
        public Repairer<InvalidFile<Void>> repairer() {
            return file -> {
                assertSame(INVALID_FILE, file);
                count.decrementAndGet();
                return success ? Stream.empty() : Stream.of(INVALID_FILE);
            };
        }
    }

}