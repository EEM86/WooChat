package ua.woochat.app;

public interface ConnectionAgent {
    public void connectionCreated(Connection data);
    public void connectionDisconnect(Connection data);
    public void receivedMessage(String text);
}
