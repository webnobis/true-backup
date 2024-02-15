package com.webnobis.truebackup.repair;

import java.util.stream.Stream;

@FunctionalInterface
public interface Repairer<T> {

    Stream<T> repair(T invalidFile);

}
