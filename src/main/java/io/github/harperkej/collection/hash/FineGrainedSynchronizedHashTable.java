package io.github.harperkej.collection.hash;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FineGrainedSynchronizedHashTable<T> {

    private int bucketsNumber;
    private final int initialBucketsNumber;
    private final int bucketCapacity;
    private List<T>[] buckets;
    private Object[] locks;

    public FineGrainedSynchronizedHashTable() {
        this.initialBucketsNumber = 16;
        this.bucketsNumber = 16;
        this.bucketCapacity = 1024;
        this.buckets = new List[this.bucketsNumber];
        this.locks = new Object[this.bucketsNumber];
        IntStream.range(0, this.bucketsNumber).forEach(i -> {
            this.buckets[i] = new ArrayList<>();
            this.locks[i] = new Object();
        });
    }

    public boolean add(T object) {
        int bucketKey = object.hashCode() % this.bucketsNumber;
        int lockKey = object.hashCode() % initialBucketsNumber;
        synchronized (locks[lockKey]) {
            List<T> bucket = this.buckets[bucketKey];
            boolean result = bucket.add(object);
            if (bucket.size() > this.bucketCapacity) {
                resize();
            }
            return result;
        }
    }

    public boolean contain(T object) {
        int lockKey = object.hashCode() % this.initialBucketsNumber;
        int bucketKey = object.hashCode() % this.bucketsNumber;
        synchronized (this.locks[lockKey]) {
            List<T> bucket = this.buckets[bucketKey];
            for (T obj : bucket) {
                if (obj.equals(object)) return true;
            }
        }
        return false;
    }

    private void resize() {
        resize(0, buckets);
    }

    private void resize(int index, List<T>[] buckets) {
        synchronized (locks[index]) {
            if (index == 0 && buckets != this.buckets)
                return;//Abort the resize, some other thread has done the resize for us.
            index++;
            if (index < this.initialBucketsNumber)
                resize(index, buckets);
            else completeResize();
        }
    }

    private void completeResize() {
        this.bucketsNumber *= 2;
        List<T>[] newBuckets = new List[this.bucketsNumber];
        IntStream.range(0, this.bucketsNumber).forEach(i -> newBuckets[i] = new ArrayList<>());
        IntStream.range(0, this.bucketsNumber / 2).forEach(i -> {
            List<T> bucket = this.buckets[i];
            for (T object : bucket) {
                int bucketKey = object.hashCode() % this.bucketsNumber;
                newBuckets[bucketKey].add(object);
            }
        });
        this.buckets = newBuckets;
    }

}
