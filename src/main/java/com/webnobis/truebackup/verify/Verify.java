package com.webnobis.truebackup.verify;

import java.util.stream.Stream;

@FunctionalInterface
public interface Verify<T> {

    Stream<T> verify();

}
