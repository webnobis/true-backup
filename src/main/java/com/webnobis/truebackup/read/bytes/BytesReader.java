package com.webnobis.truebackup.read.bytes;

import java.io.UncheckedIOException;
import java.util.stream.Stream;

/**
 * Bytes reader
 *
 * @param <R> bytes bundle return type
 * @param <T> files bundle type
 * @author Steffen Nobis
 */
public interface BytesReader<R, T> {

    /**
     * Stream of read bytes from all files parallel, each with same position, until the largest files end is reached
     *
     * @param files all files
     * @return all bytes, for shorter files the byte is null, if the end is reached
     * @throws UncheckedIOException, if the reading failed
     */
    Stream<R> read(T files);

}
