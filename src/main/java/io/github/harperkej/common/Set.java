package io.github.harperkej.common;

public interface Set<T> {

    public boolean add(Node<T> object);

    public boolean remove(Node<T> object);

    public boolean contains(Node<T> object);

}
