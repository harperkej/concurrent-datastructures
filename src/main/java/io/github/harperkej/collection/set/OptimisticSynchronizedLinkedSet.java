package io.github.harperkej.collection.set;

import io.github.harperkej.common.Node;
import io.github.harperkej.common.Set;

public class OptimisticSynchronizedLinkedSet<T> implements Set<T> {

    private Node<T> tail;
    private Node<T> head;

    public OptimisticSynchronizedLinkedSet() {
        head = new Node<>(Long.MIN_VALUE);
        tail = new Node<>(Long.MAX_VALUE);

        head.setNext(tail);
    }

    @Override
    public boolean add(Node<T> object) {
        long objectKey = object.getKey();
        while (true) {
            Node<T> predecessor = this.head;
            Node<T> successor = predecessor.getNext();
            while (successor.getKey() <= objectKey) {
                predecessor = successor;
                successor = successor.getNext();
            }
            try {
                predecessor.lock();
                successor.lock();
                if (validate(predecessor, successor)) {
                    if (successor.getKey() == objectKey) return false; // do not add the same element twice.
                    predecessor.setNext(object);
                    object.setNext(successor);
                    return true;
                }
            } finally {
                predecessor.unlock();
                successor.unlock();
            }
            predecessor.unlock();
            successor.unlock();
        }
    }

    @Override
    public boolean remove(Node<T> object) {
        long objectKey = object.getKey();
        while (true) {
            Node predecessor = this.head;
            Node current = predecessor.getNext();
            while (current.getKey() <= objectKey) {
                predecessor = current;
                current = current.getNext();
            }
            try {
                predecessor.lock();
                current.lock();
                if (validate(predecessor, current)) {
                    if (objectKey == current.getKey()) {
                        predecessor.setNext(current.getNext());
                        return true;
                    } else return false;
                }
            } finally {
                predecessor.unlock();
                current.unlock();
            }
            predecessor.unlock();
            current.unlock();
        }
    }

    @Override
    public boolean contains(Node<T> object) {
        long objectKey = object.getKey();
        while (true) {
            Node<T> predecessor = this.head;
            Node<T> current = predecessor.getNext();
            while (current.getKey() < objectKey) {
                predecessor = current;
                current = current.getNext();
            }
            try {
                predecessor.lock();
                current.lock();
                if (validate(predecessor, current)) {
                    if (current.getKey() == objectKey) return true;
                    else return false;
                }
            } finally {
                predecessor.unlock();
                current.unlock();
            }
            predecessor.unlock();
            current.unlock();
        }
    }

    /**
     * In the optimistic approach, after a thread obtains the locks on the predecessor and successor nodes,
     * the thread must check that the nodes are still in the set and are still neighbors.
     *
     * @return true if the nodes are sill in the set and are accessible, otherwise false.
     */
    private boolean validate(Node predecessor, Node successor) {
        Node node = this.head;
        while (node != null && node.getKey() <= predecessor.getKey()) {
            if (predecessor == node) return node.getNext() == successor;
            node = node.getNext();
        }
        return false;
    }

}
