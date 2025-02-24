package producerConsumer;



import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BoundedBufferTest {
    // Unit Tests
    @Test
    public void testBasicProduceConsume() throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(5);
        buffer.produce(1);
        assertEquals(Optional.of(1), buffer.consume());
    }

    @Test
    public void testConcurrent() throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(5);
        CountDownLatch latch = new CountDownLatch(20); // 10 producers + 10 consumers

        // Spawn multiple producer threads
        for(int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    buffer.produce(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
            }).start();
        }

        // Spawn multiple consumer threads
        for(int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    buffer.consume();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
            }).start();
        }

        latch.await(5, TimeUnit.SECONDS);
        assertEquals(0, buffer.getCurrentSize());
    }

    @Test
    public void testShutdown() {
        // Test shutdown behavior
    }
}
