package ua.woochat.app;

/**
 * This interface should be implemented by any class whose instances are intended to communicate between client - server, using Connection objects.
 */
public interface ConnectionAgent {
    void connectionCreated(Connection data);
    void connectionDisconnect(Connection data);
    void receivedMessage(Connection data, String text);
}
