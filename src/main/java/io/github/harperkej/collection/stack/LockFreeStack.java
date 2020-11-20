package io.github.harperkej.collection.stack;

import io.github.harperkej.common.Node;

import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack<T> {

    private AtomicReference<Node<T>> top;

    public LockFreeStack() {
        this.top = new AtomicReference<>();
    }

    public boolean push(T value) {
        Node<T> newNode = new Node<>(-1L);
        while (!tryPush(newNode)) backOff();
        return true;
    }

    public T pop() {
        while (true) {
            Node<T> node = this.top.get();
            if (node == null) throw new EmptyStackException();
            if (top.compareAndSet(node, node.getNext()))
                return node.getObject();
            backOff();
        }
    }

    private boolean tryPush(Node<T> node) {
        Node top = this.top.get();
        node.setNext(top);
        return this.top.compareAndSet(top, node);
    }

    private void backOff() {
    }

}
