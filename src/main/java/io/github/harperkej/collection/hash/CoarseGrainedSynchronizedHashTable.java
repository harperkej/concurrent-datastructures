package io.github.harperkej.collection.hash;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CoarseGrainedSynchronizedHashTable<T> {

    private List<T>[] buckets;
    private int bucketsNumber = 16;
    private final int bucketCapacity = 1024;

    public CoarseGrainedSynchronizedHashTable() {
        buckets = new List[bucketsNumber];
        IntStream.range(0, bucketsNumber).forEach(i -> buckets[i] = new ArrayList<>());
    }

    public synchronized boolean add(T object) {
        int bucketKey = object.hashCode() % buckets.length;
        List<T> bucket = this.buckets[bucketKey];
        boolean result = bucket.add(object);
        if (bucket.size() > this.bucketCapacity) {
            resizeBuckets();
        }
        return result;
    }

    public synchronized boolean contains(T object) {
        int bucketKey = object.hashCode() % this.bucketsNumber;
        List<T> bucket = this.buckets[bucketKey];
        for (T obj : bucket) {
            if (obj.equals(obj)) return true;
        }
        return false;
    }

    private void resizeBuckets() {
        this.bucketsNumber *= 2;
        List<T>[] newBuckets = new List[bucketsNumber];
        IntStream.range(0, bucketsNumber).forEach(i -> newBuckets[i] = new ArrayList<>());
        IntStream.range(0, bucketsNumber / 2).forEach(i -> {
            List<T> currentBucket = buckets[i];
            for (T object : currentBucket) {
                int bucketKey = object.hashCode() % bucketsNumber;
                newBuckets[bucketKey].add(object);
            }
        });
        this.buckets = newBuckets;
    }

}
