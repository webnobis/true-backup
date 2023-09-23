package com.webnobis.truebackup.model;

/**
 * Bundle between master and copy
 *
 * @param master the master
 * @param copy   the copy
 * @param <T>    the type
 * @author Steffen Nobis
 */
public record Bundle<T>(T master, T copy) {
}
