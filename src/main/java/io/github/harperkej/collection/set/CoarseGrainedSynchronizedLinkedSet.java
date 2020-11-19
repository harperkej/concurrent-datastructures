package io.github.harperkej.collection.set;

import io.github.harperkej.common.Node;
import io.github.harperkej.common.Set;

/**
 * Full synchronized linked set. There is at most on thread
 * at any given point in time operating on the set.
 */
public class CoarseGrainedSynchronizedLinkedSet<T> implements Set<T> {

    private Node<T> head;
    private Node<T> tail;

    public CoarseGrainedSynchronizedLinkedSet() {
        head = new Node(Long.MIN_VALUE);
        tail = new Node(Long.MAX_VALUE);
        head.setNext(tail);
    }

    /**
     * Adds a new element in the set. If the element is already
     * there, it returns false and does not insert duplicates.
     * Here it is assumed that different elements have distinct keys.
     * It is assumed that the values of the keys of the elements are in the range
     * (Long.MIN_VALUE, MAX_VALUE) - exclusive start and end.
     *
     * @param object The object to be inserted.
     * @return true in case the object is inserted, false otherwise.
     */
    @Override
    public synchronized boolean add(T object) {
        Node<T> node = new Node<T>((long) object.hashCode());
        node.setObject(object);
        Node[] objectSpot = findObjectSpot(node);
        Node<T> predecessor = objectSpot[0];
        Node<T> successor = objectSpot[1];
        long objectKey = node.getKey();
        boolean isBetween = predecessor.getKey() < objectKey
                && successor.getKey() > objectKey;
        if (isBetween) {
            predecessor.setNext(node);
            node.setNext(successor);
            return true;
        }
        return false;
    }

    /**
     * Removes a given node from the set, if the node exists.
     *
     * @param object the object to be removed.
     * @return true if the object is removed, otherwise false.
     */
    @Override
    public synchronized boolean remove(T object) {
        Node predecessor = head;
        Node current = head.getNext();
        Node<T> node = new Node<T>((long) object.hashCode());
        long objectKey = node.getKey();
        while (current != null && objectKey != current.getKey()) {
            predecessor = current;
            current = current.getNext();
        }
        if (current != null && current.getKey() == objectKey) {
            predecessor.setNext(current.getNext());
            return true;
        }
        return false;
    }

    /**
     * Traverses the set and checks if the given element exists in the set.
     *
     * @param object the object to check if it exists in the set.
     * @return true if the object exists in the set, otherwise false.
     */
    @Override
    public synchronized boolean contains(T object) {
        Node node = head.getNext();
        Node<T> newNode = new Node<>((long) object.hashCode());
        long objectKey = newNode.getKey();
        while (objectKey > node.getKey()) {
            node = node.getNext();
        }
        return node.getKey() == objectKey;
    }

    /**
     * Finds the predecessor and the successor of the given object in the linked set.
     *
     * @param object the object to find its spot in the linked set.
     * @return the predecessor and the successor of the object, if it exists in the set.
     * If it does not exist, it will simply return the last two nodes of the set.
     */
    private Node[] findObjectSpot(Node<T> object) {
        Node predecessor = head;
        Node successor = predecessor.getNext();
        Long objectKey = object.getKey();
        // Traverse the set, until the given object is supposedly
        // between its predecessor and its successor.
        // If there's no spot for the object in the set (because the set already contains the object),
        // the successor will eventually become the tail node that has Integer.MAX_VALUE as
        // its key and the loop exists.
        while (objectKey > predecessor.getKey()
                && object.getKey() > successor.getKey()) {
            predecessor = successor;
            successor = successor.getNext();
        }
        return new Node[]{predecessor, successor};
    }

}
