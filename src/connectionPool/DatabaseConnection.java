package connectionPool;

public class DatabaseConnection implements Connection{
    boolean connected;
    final String connectionId;

    public DatabaseConnection(String connectionId) {
        this.connectionId = connectionId;
        this.connected = false;
    }

    @Override
    public void connect() {
        try {
            Thread.sleep(1000);
            this.connected = true;
            System.out.println("Connection " + connectionId + " established");
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void disconnect() {

            this.connected = false;
            System.out.println("Connection " + connectionId + " closed");

    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void query(String query) {
        if(!connected) {
            throw new IllegalStateException("Connection is not establised");
        }
        System.out.println("Executing query on connection " + connectionId + " : " + query);
    }
}
