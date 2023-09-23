package com.webnobis.truebackup.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * An invalid file
 *
 * @param invalid the invalid file
 * @param valid   the valid file
 * @param bytes   the invalid bytes
 * @author Steffen Nobis
 */
public record InvalidFile(Path invalid, Path valid, List<InvalidByte> bytes) {

    /**
     * Only true, if all invalid bytes were found through voting without fail
     *
     * @return voting result
     * @see InvalidByte#votingSuccess()
     */
    public boolean votingSuccess() {
        return Optional.ofNullable(bytes).stream().flatMap(List::stream).map(InvalidByte::votingSuccess).allMatch(Boolean.TRUE::equals);
    }

    @Override
    public String toString() {
        return "InvalidFile{" +
                "invalid=" + invalid +
                ", valid=" + valid +
                ", bytes=" + bytes +
                '}';
    }
}
