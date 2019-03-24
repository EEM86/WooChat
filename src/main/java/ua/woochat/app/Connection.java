package ua.woochat.app;

public interface Connection {
    public static final int PORTCONNECT = 7777;
    public static final int PORTCHATTING = 9876;
    public void send(Message message);
    public void close();
}
