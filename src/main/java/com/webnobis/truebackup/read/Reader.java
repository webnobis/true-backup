package com.webnobis.truebackup.read;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Reader
 *
 * @param <T> each content type
 * @author Steffen Nobis
 */
@FunctionalInterface
public interface Reader<T> {

    /**
     * Read the contents of the bundle
     *
     * @param bundle the bundle
     * @return list content stream, each bundle part
     */
    Stream<List<T>> read(List<Path> bundle);

}