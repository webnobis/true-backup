package com.webnobis.truebackup.read;

import com.webnobis.truebackup.model.FileByte;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Byte bundle iterable
 *
 * @author steffen nobis
 */
public class ByteBundleIterable implements Iterator<List<FileByte>>, Iterable<List<FileByte>> {

    private final Map<Path, ByteIterator> iterators;

    private final AtomicLong positionRef;

    /**
     * Open all files for read
     *
     * @param files the files
     * @see ByteIterator#ByteIterator(Path)
     */
    public ByteBundleIterable(List<Path> files) {
        this.iterators = Objects.requireNonNull(files, "files is null").stream()
                .collect(Collectors.toUnmodifiableMap(file -> file, ByteIterator::new));
        positionRef = new AtomicLong();
    }

    /**
     * Check if any file has more bytes to read
     *
     * @return true, if any file has more bytes to read
     */
    @Override
    public boolean hasNext() {
        return iterators.values().stream().anyMatch(ByteIterator::hasNext);
    }

    /**
     * Reads from each file the next byte, maybe null, if end reached
     *
     * @return list file bytes
     */
    @Override
    public synchronized List<FileByte> next() {
        long position = positionRef.getAndIncrement();
        return iterators.entrySet().stream().map(e -> new FileByte(e.getKey(), e.getValue().next(), position)).toList();
    }

    /**
     * Itself as iterator
     *
     * @return self
     */
    @Override
    public Iterator<List<FileByte>> iterator() {
        return this;
    }
}
