package io.github.harperkej.collection.stack;

public class SequentialStack<T> {

    private Node<T> head;

    public SequentialStack() {
        this.head = new Node<>();
    }

    public void push(T element) {
        Node node = new Node<T>();
        node.setValue(element);
        Node predecessor = this.head;
        Node successor = predecessor.getNext();
        node.setNext(successor);
        predecessor.setNext(node);
    }

    public T pop() {
        if (head.getNext() == null) return null;
        Node<T> result = head.getNext();
        head.setNext(result.getNext());
        return result.getValue();
    }

    private class Node<T> {

        private T value;
        private Node<T> next;

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

}
