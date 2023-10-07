package com.webnobis.truebackup.repair;

import com.webnobis.truebackup.model.InvalidFile;

import java.util.stream.Stream;

/**
 * Repairer of invalid files
 *
 * @author Steffen Nobis
 */
@FunctionalInterface
public interface Repairer {

    /**
     * Does nothing repairer
     *
     * @return stream of over-given invalid file
     * @see Stream#of(Object)
     */
    static Repairer doesNothing() {
        return Stream::of;
    }

    /**
     * Repairs the invalid file.<br>
     * If valid file doesn't exist, invalid file will be deleted if delete flag is true, otherwise renamed
     *
     * @param invalidFile the invalid file
     * @return empty stream, if repair was success, otherwise the stream of the same invalid file
     */
    Stream<InvalidFile> repair(InvalidFile invalidFile);

}
