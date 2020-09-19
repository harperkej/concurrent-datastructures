package io.github.harperkej.collection.queue;

/**
 * Fully synchronized, bounded, blocking queue.
 * NOTE: In case your thread calls #dequeue() method and the queue is
 * empty and no other thread inserts an element in the queue, your thread can starve.
 */
public class CoarseGrainedSynchronizedBoundedBlockingQueue<T> {

    private volatile int head = 0, tail = 0;
    private T[] elements;
    private int size;

    public CoarseGrainedSynchronizedBoundedBlockingQueue(int size) {
        this.size = size;
        elements = (T[]) new Object[size];
    }

    /**
     * Inserts a new element in the queue if there is space for one more element.
     *
     * @param object The object to insert in the queue.
     */
    public synchronized void enqueue(T object) throws InterruptedException {
        while (tail == size + head) this.wait();
        elements[tail % size] = object;
        tail++;
        this.notifyAll();
    }

    /**
     * Removes the first inserted element of the queue, if there is such an element.
     *
     * @return The first inserted element of the queue.
     */
    public synchronized T dequeue() throws InterruptedException {
        while (tail == head) this.wait();
        T element = elements[head % size];
        head++;
        this.notifyAll();
        return element;
    }

    /**
     * Returns the number of the elements of the queue.
     *
     * @return the number of the elements of the queue.
     */
    public synchronized int size() {
        return tail - head;
    }

}
