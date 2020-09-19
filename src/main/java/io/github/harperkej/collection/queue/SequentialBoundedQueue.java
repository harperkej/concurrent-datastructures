package io.github.harperkej.collection.queue;

/**
 * Sequential, bounded FIFO queue.
 *
 * @param <T>
 */
public class SequentialBoundedQueue<T> {

    private T elements[];
    private final int size;

    private int head = 0, tail = 0;

    public SequentialBoundedQueue(int size) {
        this.size = size;
        elements = (T[]) new Object[size];
    }

    /**
     * Inserts a new element in the queue, if there is free space.
     * If there is no free space, it will fail to insert the element.
     *
     * @param object The object to return.
     * @return true if the element is inserted because there was free space, otherwise false.
     */
    public boolean enqueue(T object) {
        if (tail - head == size) return false;
        elements[tail % size] = object;
        tail++;
        return true;
    }

    /**
     * Returns the first inserted element in the queue. If there is no element in the queue, it will return null.
     *
     * @return the first inserted element of the queue.
     */
    public T dequeue() {
        if (tail == head) return null;
        T object = elements[head % size];
        head++;
        return object;
    }

    /**
     * Returns the number of elements in the queue
     *
     * @return the number of elements in the queue.
     */
    public int size() {
        return tail - head;
    }

}
