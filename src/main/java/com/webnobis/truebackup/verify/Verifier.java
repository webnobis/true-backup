package com.webnobis.truebackup.verify;


import java.util.List;
import java.util.stream.Stream;

@FunctionalInterface
public interface Verifier<T, B> {

    Stream<T> verify(List<B> bundle);

}
