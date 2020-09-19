package io.github.harperkej.common;

import io.github.harperkej.lock.CLHLock;

public class Node<T> {

    private T object;
    private Node<T> next;
    /**
     * The identifier of the node. Typically a hash value of the content of the node.
     */
    private long key;

    private CLHLock lock;

    public Node(long key) {
        this.key = key;
        this.lock = new CLHLock();
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

    @Override
    public String toString() {
        return "[Object: " + object + ", Key: " + key + ", next: " + (next != null ? next.key : "none" + "]");
    }

}
