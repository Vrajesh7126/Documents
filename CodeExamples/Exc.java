package CodeExamples;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Exc {
    public static void main(String[] args) {
        // Executor executor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS, new
        // LinkedBlockingQueue<>());

        // executor.execute(() -> {
        // System.out.println("Hello World");
        // });

        // ExecutorService executorService = Executors.newFixedThreadPool(2);
        // executorService.submit(() -> System.out.println("Task Running..."));
        // executorService.shutdown();
        // executorService.shutdownNow();

        // ExecutorService executorService2 = Executors.newCachedThreadPool();
        // for (int i = 1; i <= 1000; i++) {
        // executorService2.submit(() -> {
        // try {
        // Thread.sleep(1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // System.out.println("Task submitted=" + Thread.currentThread().getName());
        // });
        // }

        // executorService2.shutdown();

        // ScheduledExecutorService scheduledExecutorService =
        // Executors.newScheduledThreadPool(2);

        // scheduledExecutorService.schedule(() -> System.out.println("Called.." +
        // Thread.currentThread().getName()), 2,
        // TimeUnit.SECONDS);
        // scheduledExecutorService.schedule(() -> System.out.println("Called.." +
        // Thread.currentThread().getName()), 2,
        // TimeUnit.SECONDS);
        // scheduledExecutorService.schedule(() -> System.out.println("Called.." +
        // Thread.currentThread().getName()), 2,
        // TimeUnit.SECONDS);
        // scheduledExecutorService.schedule(() -> System.out.println("Called.." +
        // Thread.currentThread().getName()), 2,
        // TimeUnit.SECONDS);
        // scheduledExecutorService.schedule(() -> System.out.println("Called.." +
        // Thread.currentThread().getName()), 2,
        // TimeUnit.SECONDS);
        // scheduledExecutorService.schedule(() -> System.out.println("Called.." +
        // Thread.currentThread().getName()), 2,
        // TimeUnit.SECONDS);

        // ExecutorService service = Executors.newSingleThreadExecutor();
        // Callable<Integer> callable = () -> {
        // Thread.sleep(3000);
        // throw new RuntimeException("Somthing failed");
        // };

        // Future<Integer> future = service.submit(callable);
        // // try {
        // // future.get();
        // // } catch (ExecutionException e) {
        // // e.printStackTrace();
        // // }

        // try {
        // future.get(2, TimeUnit.SECONDS);
        // } catch (InterruptedException e) {
        // System.out.println("Exception 1");
        // e.printStackTrace();
        // } catch (ExecutionException e) {
        // System.out.println("Exception 2");
        // e.printStackTrace();
        // } catch (TimeoutException e) {
        // System.out.println("Exception 3");
        // e.printStackTrace();
        // }

        // service.shutdown();

        ExecutorService service = Executors.newSingleThreadExecutor();

        Callable<Integer> task = () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception occurs");
            }

            System.out.println("Running...");

            return 5;
        };

        Future<Integer> result = service.submit(task);
        
        service.shutdownNow();

        ScheduledExecutorService service2;
        service2.scheduleAtFixedRate(null, 0, 0, null)
    }
}