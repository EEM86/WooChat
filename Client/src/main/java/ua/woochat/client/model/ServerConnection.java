package ua.woochat.client.model;

import ua.woochat.app.Connection;
import ua.woochat.app.ConnectionAgent;
import ua.woochat.client.listeners.ChatFormListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerConnection implements ConnectionAgent {

    private Socket socket;
    private BufferedReader reader;
    private Connection connection;

    private ChatFormListener chatFormListener;

    public ServerConnection(ChatFormListener chatFormListener){

        this.chatFormListener = chatFormListener;

        try {
            socket = new Socket(ConfigClient.getServerIP(), ConfigClient.getPortConnection());
            reader = new BufferedReader(new InputStreamReader(System.in));
            this.connection = new Connection(this, socket);
            connectionCreated(connection);
        } catch (Exception e) {

        }
    }

    public void sendToServer(String text){
        connection.sendToOutStream(text);
    }

    @Override
    public void connectionCreated(Connection data) {
    }

    @Override
    public void connectionDisconnect(Connection data) {
    }

    @Override
    public void receivedMessage(String text) {
        chatFormListener.sendToChat(text);
    }
}
