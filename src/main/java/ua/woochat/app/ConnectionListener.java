package ua.woochat.app;

public interface ConnectionListener {
    public void connectionCreated(Connection connection);
    public void connectionClosed(Connection connection);
    public void connectionException(Connection connection, Exception exception);
    public void sendToAll(Message message);
}
