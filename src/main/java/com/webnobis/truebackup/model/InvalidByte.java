package com.webnobis.truebackup.model;

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
    public String toString() {
        return "InvalidByte{" +
                "invalid=" + invalid +
                ", valid=" + valid +
                ", position=" + position +
                ", votingSuccess=" + votingSuccess +
                '}';
    }
}
