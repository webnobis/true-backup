package com.webnobis.truebackup.read;

import com.webnobis.truebackup.TempDirExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirExtension.class)
class StandardFileReaderTest {

    private static final Byte[] BYTES = {-1, 0, Byte.MIN_VALUE, Byte.MAX_VALUE, null};

    private Reader<Path> reader;

    private Reader<Path> readerWithFilter;

    @BeforeEach
    void setUp() {
        reader = new StandardFileReader(null);
        readerWithFilter = new StandardFileReader("^[a-zA-Z]+$");
    }

    @Test
    void read(Path tmpDir) throws IOException {
        Path dir1 = Files.createTempDirectory(tmpDir, StandardFileReaderTest.class.getSimpleName());
        Path dir2 = Files.createTempDirectory(tmpDir, StandardFileReaderTest.class.getSimpleName());
        Path dir3 = Files.createTempDirectory(tmpDir, StandardFileReaderTest.class.getSimpleName());
        Path file1 = Path.of("a", "first", "file.txt");
        Path file2 = Path.of("an", "other", "file.dat");
        Path file3 = Path.of("and", "an", "last", "file.xml");
        Path file4 = Path.of("the", "last", "ever", "best", "file.xml");
        Stream.of(dir1.resolve(file1), dir2.resolve(file2), dir3.resolve(file3), dir3.resolve(file4))
                .forEach(file -> {
                    try {
                        Files.createDirectories(file.getParent());
                        Files.createFile(file);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });

        List<List<Path>> list = reader.read(List.of(dir1, dir2, dir3)).toList();
        assertSame(4, list.size());
        list.forEach(files -> {
            assertSame(3, files.size());
            Stream.of(dir1, dir2, dir3).map(Path::toString).forEach(dir -> {
                assertTrue(files.stream().map(Path::toString).anyMatch(file -> file.startsWith(dir)));
            });
        });
        Stream.of(file1, file2, file3, file4).map(Path::toString).forEach(relative -> {
            assertEquals(3L, list.stream().flatMap(List::stream).map(Path::toString).filter(file -> file.endsWith(relative)).count());
        });
    }

    @Test
    void readWithFilter(Path tmpDir) throws IOException {
        Path theDir = tmpDir.resolve("the-dir");
        Path everFoundSubDir = theDir.resolve("evermatchingregex");
        Path notFoundSubDir = theDir.resolve("notmatching9regex");
        Stream.of(everFoundSubDir, notFoundSubDir).forEach(dir -> {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        Path filefound1 = Files.createTempFile(theDir, StandardFileReaderTest.class.getSimpleName(), ".json");
        Path filefound2 = Files.createTempFile(everFoundSubDir, StandardFileReaderTest.class.getSimpleName(), ".txt");
        Path notfound = Files.createTempFile(notFoundSubDir, StandardFileReaderTest.class.getSimpleName(), ".hugo");

        List<List<Path>> list = readerWithFilter.read(List.of(theDir)).toList();
        assertSame(2, list.size());
        list.forEach(files -> {
            assertSame(1, files.size());
            assertTrue(files.get(0).toString().startsWith(theDir.toString()));
        });
        Stream.of(filefound1, filefound2).map(Path::toString).forEach(relative -> {
            assertEquals(1L, list.stream().flatMap(List::stream).map(Path::toString).filter(file -> file.endsWith(relative)).count());
        });
    }
}