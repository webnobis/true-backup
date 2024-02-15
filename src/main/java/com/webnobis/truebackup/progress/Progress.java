package com.webnobis.truebackup.progress;

import com.webnobis.truebackup.model.InvalidFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Progress for changes of current work
 *
 * @param <T> the bundle type
 * @author Steffen Nobis
 */
public interface Progress<T> {

    /**
     * Progress
     *
     * @param returning the returning value
     * @param found     increment found, if true
     * @param working   increment working, if true, otherwise decrement working
     * @param <R>       the returning type
     * @return the over-giving value
     */
    <R> R progress(R returning, boolean found, boolean working);

    /**
     * Reads the bundle
     *
     * @param bundle the returning bundle
     * @return the over-given bundle
     * @see #progress(Object, boolean, boolean)
     */
    default List<Path> read(List<Path> bundle) {
        return progress(bundle, true, true);
    }

    /**
     * Repairs the invalid file
     *
     * @param invalidFile the returning invalid file
     * @return the over-given invalid file
     * @see #progress(Object, boolean, boolean)
     */
    default InvalidFile<T> repair(InvalidFile<T> invalidFile) {
        return progress(invalidFile, false, true);
    }

    /**
     * Verified stream
     *
     * @param stream the returning stream
     * @return the over-given stream
     * @see #repaired(Stream)
     */
    default Stream<InvalidFile<T>> verified(Stream<InvalidFile<T>> stream) {
        return progress(stream, false, false);
    }

    /**
     * Repaired stream
     *
     * @param stream the returning stream
     * @return the over-given stream
     * @see #progress(Object, boolean, boolean)
     */
    default Stream<InvalidFile<T>> repaired(Stream<InvalidFile<T>> stream) {
        return progress(stream, false, false);
    }

}
