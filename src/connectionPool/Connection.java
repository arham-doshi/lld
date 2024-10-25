package connectionPool;

public interface Connection {
    void connect();
    void disconnect();
    boolean isConnected();
    void query(String query);
}
