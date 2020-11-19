package io.github.harperkej;

import io.github.harperkej.collection.set.OptimisticSynchronizedLinkedSet;
import io.github.harperkej.common.CustomThread;
import io.github.harperkej.common.Node;
import io.github.harperkej.common.Set;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws InterruptedException {

        List<Node<Integer>> nodes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Node node = new Node(i);
            node.setObject(i);
            nodes.add(node);
        }

        Set<Integer> set = new OptimisticSynchronizedLinkedSet<>();

        MyThread[] threads = new MyThread[4];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new MyThread(i, nodes, set);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
    }

    public static class MyThread extends CustomThread {

        private int id;
        private List<Node<Integer>> nodes;
        private Set set;

        public MyThread(int id, List<Node<Integer>> nodes, Set<Integer> set) {
            super(id);
            this.id = id;
            this.nodes = nodes;
            this.set = set;
        }

        @Override
        public void run() {
            Random random = new Random();
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                int operation = random.nextInt(3);
                switch (operation) {
                    case 0:
                        set.add(node);
                        break;
                    case 1:
                        set.remove(node);
                        break;
                    case 2:
                        set.contains(node);
                        break;
                    default:
                        System.out.println("Something unexpected.");
                }
            }
        }

        @Override
        public long getId() {
            return id;
        }
    }

}
