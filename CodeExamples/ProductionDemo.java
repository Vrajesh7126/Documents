import java.util.ArrayList;
import java.util.List;

public class ProductionDemo {
    private static final Object LOCK = new Object();

    static List<byte[]> list = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        
        System.out.println("PID = " + ProcessHandle.current().pid());

        // CPU Heavy Thread
        Thread cpuThread = new Thread(() -> {
            System.out.println("CPU thread started");

            while (true) {
                double result = Math.sqrt(Math.random() * Math.random());
            }
        }, "cpu-heavy-thread");

        Thread lockHolder = new Thread(() -> {
            synchronized (LOCK) {
                System.out.println("Lock holder aquired LOCK");

                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Interrupted Exception occurs");
                }
            }
        }, "lock-holder");

        Thread blocked1 = new Thread(() -> {
            synchronized (LOCK){
                System.out.println("blocked-1 aquired LOCK");
            }
        }, "blocked-1");

        Thread blocked2 = new Thread(() -> {
            synchronized (LOCK){
                System.out.println("blocked-2 aquired LOCK");
            }
        }, "blocked-2");

        // Memory allocation
        Thread memoryThread = new Thread(() -> {

            while (true) {
                byte[] data = new byte[1024 * 128];

                list.add(data);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Interrupted Exception occurs");
                    return;
                }
            }
        }, "memory-thread");

        cpuThread.start();
        lockHolder.start();

        blocked1.start();
        blocked2.start();

        memoryThread.start();

        Thread.currentThread().join();
    }
}
