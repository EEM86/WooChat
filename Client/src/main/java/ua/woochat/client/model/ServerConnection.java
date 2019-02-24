package ua.woochat.client.model;

import ua.woochat.app.Connection;
import ua.woochat.app.ConnectionAgent;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerConnection implements ConnectionAgent {

    private Socket socket;
    private BufferedReader reader;
    private Connection connection;

    private ActionListener chatFormListener;

    public ServerConnection(ActionListener chatFormListener){

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
        System.out.println("message " + text);
        //chatFormListener.sendToChat(text);
    }

}
