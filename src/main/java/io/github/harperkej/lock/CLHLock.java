package io.github.harperkej.lock;

import io.github.harperkej.common.Lock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * FIFO queue based lock. Efficient, FIFO fairness and low memory consumption.
 * Not efficient for NUMA architecture because of remote spinning.
 */
public class CLHLock implements Lock {

    private AtomicReference<Node> tail = new AtomicReference<>(new Node(false));

    private ThreadLocal<Node> currentThreadNode = new ThreadLocal<>();

    @Override
    public void lock() {
        Node node = new Node();
        Node previousNode = this.tail.getAndSet(node);
        while (previousNode.lock.get()){ }
        currentThreadNode.set(node);
    }

    @Override
    public void unlock() {
        Node currentThreadNode = this.currentThreadNode.get();
        currentThreadNode.lock.set(false);
    }

    private static class Node {
        AtomicBoolean lock = new AtomicBoolean(true);

        public Node() { }

        public Node(boolean initialValue) {
            this.lock.set(initialValue);
        }
    }

}
