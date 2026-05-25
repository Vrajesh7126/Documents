package CodeExamples;

import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierExample {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3);

        Thread t1 = new Thread(() -> {
            try {
                System.out.println("T1 waiting");
                barrier.await();
            } catch (Exception e) {
                System.out.println("T1 exception occurs");
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                System.out.println("T2 waiting");
                barrier.await();
            } catch (Exception e) {
                System.out.println("T2 exception occurs");
            }
        });

        t1.start();
        t2.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t1.interrupt();
    }
}
