package io.github.harperkej.lock;

import io.github.harperkej.common.Lock;

import java.util.concurrent.atomic.AtomicBoolean;

public class TTASLock implements Lock {

    private AtomicBoolean lock = new AtomicBoolean(false);

    @Override
    public void lock() {
        while (true) {
            while (lock.get()) { }
            if (!lock.getAndSet(true)) return;
        }
    }

    @Override
    public void unlock() {
        lock.set(false);
    }
}
