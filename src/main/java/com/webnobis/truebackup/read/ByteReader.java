package com.webnobis.truebackup.read;

import com.webnobis.truebackup.model.FileByte;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Byte reader
 *
 * @author steffen nobis
 */
public interface ByteReader extends Reader<FileByte> {

    /**
     * Read the bytes of the files
     *
     * @param files files
     * @return list file bytes stream, each file
     */
    @Override
    default Stream<List<FileByte>> read(List<Path> files) {
        return StreamSupport.stream(new ByteBundleIterable(files).spliterator(), false);
    }
}
