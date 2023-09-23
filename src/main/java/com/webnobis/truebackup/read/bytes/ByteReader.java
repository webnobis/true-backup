package com.webnobis.truebackup.read.bytes;

import java.io.IOException;

/**
 * Byte reader
 *
 * @author Steffen Nobis
 */
public interface ByteReader {

    /**
     * Reads the next byte
     *
     * @return next byte if available, otherwise null
     * @throws IOException, if the reading failed
     */
    Byte readNext() throws IOException;

}
