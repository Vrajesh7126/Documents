package CodeExamples;

import java.util.concurrent.locks.ReentrantLock;

public class MyClass {
    static ReentrantLock lock1 = new ReentrantLock();
    static ReentrantLock lock2 = new ReentrantLock();

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            while (true) {
                if (lock1.tryLock()) {
                    System.out.println("T1 got lock1");

                    if (lock2.tryLock()) {
                        System.out.println("T1 got a lock2");
                        break;
                    } else {
                        System.out.println("T1 releasing lock1");
                        lock1.unlock();
                    }
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                if (lock2.tryLock()) {
                    System.out.println("T2 got lock2");

                    if (lock1.tryLock()) {
                        System.out.println("T2 got lock1");
                        break;
                    } else {
                        System.out.println("T2 releasing lock2");
                        lock2.unlock();
                    }
                }
            }
        });

        t1.start();
        t2.start();
    }
}