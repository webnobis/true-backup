package com.webnobis.truebackup.read;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Objects;

/**
 * Byte iterator
 *
 * @author steffen nobis
 */
public class ByteIterator implements Iterator<Byte> {

    private static final int CAPACITY = 256;

    private static final Logger log = LoggerFactory.getLogger(ByteIterator.class);

    private final ReadableByteChannel channel;

    private final ByteBuffer buffer;

    /**
     * Opens the file for read
     *
     * @param file file
     */
    public ByteIterator(Path file) {
        try {
            if (Files.exists(Objects.requireNonNull(file, "file is null")) && Files.size(file) > 0L) {
                channel = FileChannel.open(file, StandardOpenOption.READ);
                buffer = ByteBuffer.allocate(CAPACITY);
                channel.read(buffer);
                buffer.flip();
            } else {
                channel = new ReadableByteChannel() {
                    @Override
                    public int read(ByteBuffer dst) {
                        return -1;
                    }

                    @Override
                    public boolean isOpen() {
                        return false;
                    }

                    @Override
                    public void close() {
                    }
                };
                buffer = null;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Check if more bytes available
     *
     * @return true, if more bytes available
     */
    @Override
    public boolean hasNext() {
        if (!channel.isOpen()) {
            return false;
        }
        if (buffer.hasRemaining()) {
            return true;
        }
        try {
            buffer.clear();
            if (channel.read(buffer) > -1) {
                buffer.flip();
                return true;
            } else {
                channel.close();
                return false;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reads the next byte
     *
     * @return the next byte, otherwise null
     */
    @Override
    public Byte next() {
        if (hasNext()) {
            return buffer.get();
        } else {
            return null;
        }
    }
}
