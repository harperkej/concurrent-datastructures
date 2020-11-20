package io.github.harperkej.collection.set;

import io.github.harperkej.common.Node;
import io.github.harperkej.common.Set;

/**
 * Fine grained synchronized linked set.
 * A thread obtains the lock of an element and then moves on to obtain
 * the lock of the next element in th set and so on it travers the set by obtaining the locks of elements in a hand by hand manner.
 */
public class FineGrainedSynchronizedLinkedSet<T> implements Set<T> {

    private Node<T> head;
    private Node<T> tail;

    public FineGrainedSynchronizedLinkedSet() {
        this.head = new Node<>(Long.MIN_VALUE);
        this.tail = new Node<>(Long.MAX_VALUE);

        this.head.setNext(tail);
    }

    /**
     * Adds a new element in the set. If the element is already
     * there, it returns false and does not insert duplicates.
     * Here it is assumed that different elements have distinct keys.
     * It is assumed that the values of the keys of the elements are in the range
     * (Long.MIN_VALUE, MAX_VALUE) - exclusive start and end.
     * <p>
     * Multiple threads execute this method simultaneously,
     * but each thread locks at most two elements at any point in time and threads
     * probably can not overtake each other.
     *
     * @param object The object to be inserted.
     * @return true in case the object is inserted, false otherwise.
     */
    @Override
    public boolean add(T object) {
        Node predecessor = null, successor = null;
        Node<T> newNode = new Node<>((long) object.hashCode());
        newNode.setObject(object);
        long objectKey = newNode.getKey();
        try {
            predecessor = this.head;
            predecessor.lock();
            successor = predecessor.getNext();
            successor.lock();
            while (successor != null) {
                if (predecessor.getKey() < objectKey && successor.getKey() > objectKey) {
                    predecessor.setNext(newNode);
                    newNode.setNext(successor);
                    return true;
                }
                predecessor.unlock();
                predecessor = successor;
                successor = successor.getNext();
                if (successor != null) successor.lock();
            }
        } finally {
            predecessor.unlock();
            if (successor != null) successor.unlock();
        }
        return false;
    }

    /**
     * Removes a given node from the set, if the node exists in the set.
     * <p>
     * As in the case of the <add/> method, multiple threads can execute this method simultaneously,
     * but each thread controls at most two elements at any point in time.
     *
     * @param object the object to be removed.
     * @return true if the object is removed, otherwise false.
     */
    @Override
    public boolean remove(T object) {
        Node predecessor = null, current = null;
        Node<T> node = new Node<>((long) object.hashCode());
        node.setObject(object);
        Long objectKey = node.getKey();
        try {
            predecessor = this.head;
            predecessor.lock();
            current = predecessor.getNext();
            current.lock();
            while (current != null && current.getKey() <= objectKey) {
                if (objectKey.equals(current.getKey())) {
                    predecessor.setNext(current.getNext());
                    return true;
                }
                predecessor.unlock();
                predecessor = current;
                current = current.getNext();
                if (current != null) current.lock();
            }
        } finally {
            predecessor.unlock();
            if (current != null) current.unlock();
        }
        return false;
    }

    /**
     * Traverses the set and checks if the given element exists in the set.
     * <p>
     * The method does not block other threads that want to access the set, only one
     * element at any point in time is locked by this method.
     *
     * @param object the object to check if it exists in the set.
     * @return true if the object exists in the set, otherwise false.
     */

    @Override
    public boolean contains(T object) {
        Node current = null;
        Node<T> node = new Node<>((long) object.hashCode());
        node.setObject(object);
        Long objectKey = node.getKey();
        try {
            current = this.head.getNext();
            current.lock();
            while (current != null && objectKey >= current.getKey()) {
                if (objectKey.equals(current.getKey())) {
                    return true;
                }
                current.unlock();
                current = current.getNext();
                if (current != null) current.lock();
            }
        } finally {
            if (current != null) current.unlock();
        }
        return false;
    }
}
