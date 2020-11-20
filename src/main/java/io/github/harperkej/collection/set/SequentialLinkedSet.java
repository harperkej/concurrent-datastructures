package io.github.harperkej.collection.set;

import io.github.harperkej.common.Node;
import io.github.harperkej.common.Set;

public class SequentialLinkedSet<T> implements Set<T> {

    private Node<T> head;
    private Node<T> tail;

    public SequentialLinkedSet() {
        this.head = new Node<>(Long.MIN_VALUE);
        this.tail = new Node<>(Long.MAX_VALUE);
        this.head.setNext(tail);
    }

    @Override
    public boolean add(T object) {
        Node newNode = new Node((long) object.hashCode());
        newNode.setObject(object);
        Long objectKey = newNode.getKey();
        Node predecessor = this.head;
        Node successor = predecessor.getNext();
        while (objectKey > successor.getKey()) {
            predecessor = successor;
            successor = successor.getNext();
        }
        if (objectKey.equals(successor.getKey())) return false;
        predecessor.setNext(newNode);
        newNode.setNext(successor);
        return true;
    }

    @Override
    public boolean remove(T object) {
        Node<T> node = new Node<>((long) object.hashCode());
        node.setObject(object);
        Long objectKey = node.getKey();
        Node<T> predecessor = this.head;
        Node<T> current = predecessor.getNext();
        while (objectKey > current.getKey()) {
            predecessor = current;
            current = predecessor.getNext();
        }
        if (current.getKey().equals(objectKey)) {
            predecessor.setNext(current.getNext());
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(T object) {
        Node<T> node = new Node<T>((long) object.hashCode());
        node.setObject(object);
        Long objectKey = node.getKey();
        Node<T> current = this.head;
        while (objectKey > current.getKey()) {
            current = current.getNext();
        }
        return objectKey.equals(current.getKey());
    }
}
