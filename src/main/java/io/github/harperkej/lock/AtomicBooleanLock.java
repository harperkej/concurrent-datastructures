package io.github.harperkej.lock;

import io.github.harperkej.common.Lock;

import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanLock implements Lock {

    private AtomicBoolean lock = new AtomicBoolean(false);

    public void lock() {
        while (lock.getAndSet(true)) { }
    }

    public void unlock() {
        this.lock.set(false);
    }

}
