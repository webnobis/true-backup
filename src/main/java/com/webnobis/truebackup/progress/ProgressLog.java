package com.webnobis.truebackup.progress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Progress for changes triggered logging of current work
 *
 * @param <T> the bundle type
 * @author Steffen Nobis
 */
public class ProgressLog<T> implements Progress<T> {

    private static final Logger log = LoggerFactory.getLogger(ProgressLog.class);

    final AtomicLong foundRef = new AtomicLong();
    final AtomicLong workingRef = new AtomicLong();
    private final Lock lock = new ReentrantLock();

    @Override
    public <R> R progress(R returning, boolean found, boolean working) {
        for (; ; ) {
            try {
                if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                    foundRef.addAndGet(found ? 1 : 0);
                    long value = workingRef.addAndGet(working ? 1 : -1);
                    if (value < 0) {
                        workingRef.set(0);
                    }
                    break;
                } else {
                    continue;
                }
            } catch (InterruptedException e) {
                continue;
            } finally {
                lock.unlock();
            }
        }
        log.info("{} of {} in process", workingRef.get(), foundRef.get());
        return returning;
    }
}
