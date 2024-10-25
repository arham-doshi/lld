package ratelimit;

public class RateLimitRunner {
    public static void run() {
        // Create a token bucket with capacity of 5 tokens and refill rate of 2 tokens per second
        TokenBucket rateLimiter = new TokenBucket(5, 2);

        // Create multiple threads to simulate concurrent requests
        int numberOfThreads = 10;
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {  // Each thread will make 3 requests
                    String threadName = Thread.currentThread().getName();
                    boolean allowed = rateLimiter.processRequest("test-url");
                    int remaining = rateLimiter.requestRemaining("test-url");

                    System.out.println(String.format(
                            "Thread: %s, Request %d, Allowed: %b, Remaining tokens: %d, Time: %d ms",
                            threadName, j + 1, allowed, remaining, System.currentTimeMillis() % 10000
                    ));

                    try {
                        // Sleep for a random time between 100-500ms to simulate real-world scenarios
                        Thread.sleep((long) (Math.random() * 400 + 500));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "Thread-" + i);
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
