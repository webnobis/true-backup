package com.webnobis.truebackup.model;

import java.util.Objects;

/**
 * An invalid byte
 *
 * @param invalid       the invalid file byte
 * @param valid         the valid file byte
 * @param position      the bytes position
 * @param votingSuccess the invalid byte was found through voting without fail
 * @author Steffen Nobis
 */
public record InvalidByte(FileByte invalid, FileByte valid, long position, boolean votingSuccess) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvalidByte that = (InvalidByte) o;
        return position == that.position && votingSuccess == that.votingSuccess && Objects.equals(invalid, that.invalid) && Objects.equals(valid, that.valid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invalid, valid, position, votingSuccess);
    }

    @Override
    public String toString() {
        return "InvalidByte{" +
                "invalid=" + invalid +
                ", valid=" + valid +
                ", position=" + position +
                ", votingSuccess=" + votingSuccess +
                '}';
    }
}
