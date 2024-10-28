package concurrenrLogger;

public class Log {
    public String message;
    public LogLevel level;
    public String thread;
    public Long  timestamp;

    public Log(String message, LogLevel level) {
        this.message = message;
        this.level = level;
        this.thread = Thread.currentThread().getName();
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Log{" +
                "message='" + message + '\'' +
                ", level=" + level +
                ", thread='" + thread + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getThread() {
        return thread;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
