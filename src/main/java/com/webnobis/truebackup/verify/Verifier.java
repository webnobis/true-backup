package com.webnobis.truebackup.verify;

import com.webnobis.truebackup.model.InvalidFile;

import java.io.UncheckedIOException;
import java.util.stream.Stream;

/**
 * Files verifier
 *
 * @param <T> bundle type
 * @author Steffen Nobis
 */
@FunctionalInterface
public interface Verifier<T> {

    /**
     * Stream of all invalid files
     *
     * @param files the bundle of files
     * @param files
     * @return all invalid files
     * @return
     * @throws UncheckedIOException, if verify failed
     */
    Stream<InvalidFile> verify(T files);

}
