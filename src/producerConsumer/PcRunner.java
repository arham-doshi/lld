package producerConsumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;

class BoundedBuffer<T> {
    private final BlockingQueue<T> buffer;
    private final int capacity;
    private final ReentrantLock lock;
    private boolean isShut;

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new ArrayBlockingQueue<>(this.capacity);
        // What specific BlockingQueue implementation would you use here?
        // Why?
        this.lock = new ReentrantLock();
        this.isShut = false;
    }

    public void produce(T item) throws InterruptedException {
        lock.lock();
        try{
            if(isShut) {
                throw new InterruptedException("Queue shut down");
            }
        }
        finally {
            lock.unlock();
        }
        this.buffer.put(item);
    }

    public T consume() throws InterruptedException {
        lock.lock();
        try{
            if(isShut) {
                throw new InterruptedException("Queue shut down");
            }
        }
        finally {
            lock.unlock();
        }
        return this.buffer.take();
    }

    // Produce with timeout
    public boolean produce(T item, long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        try{
            if(isShut) {
                throw new InterruptedException("Queue shut down");
            }
        }
        finally {
            lock.unlock();
        }
        return this.buffer.offer(item, timeout, unit );
    }

    // Consume with timeout
    public T consume(long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        try{
            if(isShut) {
                throw new InterruptedException("Queue shut down");
            }
        }
        finally {
            lock.unlock();
        }
        return this.buffer.poll(timeout, unit);
    }

    // Monitoring methods
    public int getCurrentSize() {
        // Implementation needed
        return this.capacity - this.buffer.remainingCapacity();
    }

    public int getRemainingCapacity() {
        // Implementation needed
        return this.buffer.remainingCapacity();
    }

    // Shutdown related methods
    public void shutdown() {
        lock.lock();
        try{
            this.isShut = true;
        }
        finally {
            lock.unlock();
        }
    }

    public boolean isShutdown() {
        lock.lock();
        try{
            return this.isShut;
        }
        finally {
            lock.unlock();
        }
    }
}


public class PcRunner {
    public static void run() {
        BoundedBuffer pc = new BoundedBuffer(2);

    }
}
