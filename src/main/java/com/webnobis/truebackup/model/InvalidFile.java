package com.webnobis.truebackup.model;

import java.nio.file.Files;
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

    private static boolean firstExistsAndSecondNot(Path file1, Path file2) {
        return Optional.ofNullable(file1).filter(Files::exists).flatMap(unused -> Optional.ofNullable(file2))
                .map(Files::notExists).orElse(false);
    }

    /**
     * True, if the invalid file should be created
     *
     * @return check result
     */
    public boolean shouldBeCreated() {
        return firstExistsAndSecondNot(valid, invalid);
    }

    /**
     * True, if the invalid file should be deleted
     *
     * @return check result
     */
    public boolean shouldBeDeleted() {
        return firstExistsAndSecondNot(invalid, valid);
    }

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
