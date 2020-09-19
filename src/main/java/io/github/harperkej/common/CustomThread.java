package io.github.harperkej.common;

/**
 * Simple class that can be use instead of the java based Thread
 * class on which you can assign a custom identifier to the thread.
 */
public class CustomThread extends Thread {

    private int id;

    public CustomThread(int id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return this.id;
    }

}
