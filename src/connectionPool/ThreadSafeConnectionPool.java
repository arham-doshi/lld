package connectionPool;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeConnectionPool {
    private final Connection[] connections;
    private final ReentrantLock[] connectionLocks;
    private final boolean[] connectionInUse;
    private final int maxConnections;
    private final int bufferSize;
    private final AtomicInteger totalConnections;
    private final AtomicInteger connectionsInUse;
    private final AtomicInteger connectionsInBuffer;
    private final ReentrantLock poolLock;
    private final Semaphore connectionsSemaphore;

    public ThreadSafeConnectionPool(int maxConnections, int initialBufferSize) {
        if(initialBufferSize > maxConnections) {
            throw new IllegalArgumentException("Buffer size cannot be larger than max connections");
        }
        this.connections = new DatabaseConnection[maxConnections];
        this.connectionLocks = new ReentrantLock[maxConnections];
        this.connectionInUse = new boolean[maxConnections];

        this.maxConnections = maxConnections;
        this.bufferSize = initialBufferSize;

        this.totalConnections = new AtomicInteger(0);
        this.connectionsInUse = new AtomicInteger(0);
        this.connectionsInBuffer = new AtomicInteger(0);
        this.poolLock = new ReentrantLock();
        this.connectionsSemaphore = new Semaphore(maxConnections, true);

        // Initialize locks
        for(int i = 0; i < maxConnections; i++) {
            this.connectionLocks[i] = new ReentrantLock();
        }

        // Create initial buffer connection
        for(int i = 0; i < bufferSize; i++){
            createConnection(i);
        }
    }

    private void createConnection(int index) {
        connectionLocks[index].lock();
        try {
            Connection newConnection = new DatabaseConnection("conn-"+index);
            newConnection.connect();
            totalConnections.incrementAndGet();
            connectionsInBuffer.incrementAndGet();
        }
        finally {
            connectionLocks[index].lock();
        }
    }

    public Connection getConnection(long timeOut) throws InterruptedException{
        if(!connectionsSemaphore.tryAcquire(timeOut, TimeUnit.MILLISECONDS)){
            throw new InterruptedException("Connection not available within timeout period");
        }
        poolLock.lock();
        try {
            // First try to get a connection from buffer
            for (int i = 0; i < maxConnections; i++) {
                if (connectionLocks[i].tryLock()) {
                    try {
                        if (connections[i] != null && !connectionInUse[i]) {
                            connectionInUse[i] = true;
                            connectionsInBuffer.decrementAndGet();
                            connectionsInUse.incrementAndGet();
                            return connections[i];
                        }
                    } finally {
                        connectionLocks[i].unlock();
                    }
                }
            }

            // If no buffered connection available, create new one
            for (int i = 0; i < maxConnections; i++) {
                if (connectionLocks[i].tryLock()) {
                    try {
                        if (connections[i] == null) {
                            createConnection(i);
                            connectionInUse[i] = true;
                            connectionsInBuffer.decrementAndGet();
                            connectionsInUse.incrementAndGet();
                            return connections[i];
                        }
                    } finally {
                        connectionLocks[i].unlock();
                    }
                }
            }

            throw new IllegalStateException("No connection available");
        } finally {
            poolLock.unlock();
        }

    }


    public void releaseConnection(Connection conn) {
        poolLock.lock();
        try {
            for (int i = 0; i < maxConnections; i++) {
                if (connectionLocks[i].tryLock()) {
                    try {
                        if (connections[i] == conn && connectionInUse[i]) {
                            connectionInUse[i] = false;
                            connectionsInUse.decrementAndGet();

                            // If buffer is full, close the connection
                            if (connectionsInBuffer.get() >= bufferSize) {
                                connections[i].disconnect();
                                connections[i] = null;
                                totalConnections.decrementAndGet();
                            } else {
                                connectionsInBuffer.incrementAndGet();
                            }

                            connectionsSemaphore.release();
                            return;
                        }
                    } finally {
                        connectionLocks[i].unlock();
                    }
                }
            }
        } finally {
            poolLock.unlock();
        }
    }


    public int getAvailableConnections() {
        return maxConnections - connectionsInUse.get();
    }


}
