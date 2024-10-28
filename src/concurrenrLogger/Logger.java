package concurrenrLogger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Logger {
    private final BufferedWriter logWriter;
    private final BlockingQueue<Log> queue;
    private final int bufferSize;
    private final ScheduledExecutorService bufferScheduler;


    public Logger(String outputFile) throws  IOException {
        this.logWriter = new BufferedWriter(new FileWriter(outputFile, true));
        this.queue = new LinkedBlockingDeque<>() ;
        this.bufferSize = 10;
        this.bufferScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    private void schedule() {
            this.bufferScheduler.scheduleAtFixedRate(this::writeBuffer, 1, 1, TimeUnit.MINUTES);
    }


    public Logger() throws  IOException{
        this("log.txt");
    }

    // methods log
    public void log(String content, LogLevel level) {
        Log log = new Log(content, level);

        queue.add(log);

        if(queue.size() > bufferSize) {
            writeBuffer();
        }

    }
    private synchronized void writeBuffer() {
        try {
            while (!queue.isEmpty()) {
                Log data = queue.take();
                this.logWriter.write(data.toString());

            }
            this.logWriter.flush();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Buffer writing was interrupted: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }



    public void shutdown() throws IOException {
        writeBuffer();
        this.bufferScheduler.shutdown();
        this.logWriter.close();
    }


}
