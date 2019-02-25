package ua.woochat.app;

public interface ConnectionAgent {
    void connectionCreated(Connection data);
    void connectionDisconnect(Connection data);
    void receivedMessage(String text);
}
