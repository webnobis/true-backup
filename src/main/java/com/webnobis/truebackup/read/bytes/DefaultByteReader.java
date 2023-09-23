package com.webnobis.truebackup.read.bytes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Default byte reader
 *
 * @author Steffen Nobis
 */
public class DefaultByteReader implements ByteReader {

    static final int CAPACITY = 256;

    private static final Logger log = LoggerFactory.getLogger(DefaultByteReader.class);

    private final Path file;

    private final ReadableByteChannel channel;

    private final ByteBuffer buffer;

    /**
     * Opens the file for reading
     *
     * @param file the file
     * @throws UncheckedIOException, if the file isn't readable
     */
    public DefaultByteReader(Path file) {
        this.file = file;
        channel = Optional.ofNullable(file).filter(Files::exists).filter(Files::isRegularFile).<ReadableByteChannel>map(f -> {
            try {
                return Files.size(f) > 0 ? FileChannel.open(f) : null;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new UncheckedIOException(e);
            }
        }).orElse(Channels.newChannel(new ByteArrayInputStream(new byte[0])));
        buffer = ByteBuffer.allocateDirect(CAPACITY);
    }

    @Override
    public synchronized Byte readNext() throws IOException {
        if (channel.isOpen()) {
            if (buffer.hasRemaining()) {
                return buffer.get();
            }
            buffer.clear();
            channel.read(buffer);
            buffer.flip();
            if (!buffer.hasRemaining()) {
                log.debug("reading of file {} finished", file);
                channel.close();
            }
        }
        return null;
    }

}
