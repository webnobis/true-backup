package com.webnobis.truebackup.progress;

import com.webnobis.truebackup.model.InvalidFile;

import java.util.stream.Stream;

/**
 * Progress for changes of current work
 *
 * @author Steffen Nobis
 */
public interface Progress {

    /**
     * Progress
     *
     * @param returning the returning value
     * @param found     increment found, if true
     * @param working   increment working, if true, otherwise decrement working
     * @param <T>       the returning type
     * @return the over-giving value
     */
    <T> T progress(T returning, boolean found, boolean working);

    /**
     * Reads the bundle
     *
     * @param bundle the returning bundle
     * @param <T>    the bundle type
     * @return the over-given bundle
     * @see #progress(Object, boolean, boolean)
     */
    default <T> T read(T bundle) {
        return progress(bundle, true, true);
    }

    /**
     * Repairs the invalid file
     *
     * @param invalidFile the returning invalid file
     * @return the over-given invalid file
     * @see #progress(Object, boolean, boolean)
     */
    default InvalidFile repair(InvalidFile invalidFile) {
        return progress(invalidFile, false, true);
    }

    /**
     * Verified stream
     *
     * @param stream the returning stream
     * @return the over-given stream
     * @see #repaired(Stream)
     */
    default Stream<InvalidFile> verified(Stream<InvalidFile> stream) {
        return repaired(stream);
    }

    /**
     * Repaired stream
     *
     * @param stream the returning stream
     * @return the over-given stream
     * @see #progress(Object, boolean, boolean)
     */
    default Stream<InvalidFile> repaired(Stream<InvalidFile> stream) {
        return progress(stream, false, false);
    }

}
