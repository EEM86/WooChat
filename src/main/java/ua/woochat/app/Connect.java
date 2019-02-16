package ua.woochat.app;

public interface Connect {
    void sendToOutStream(String text);
    void disconnect();
}
