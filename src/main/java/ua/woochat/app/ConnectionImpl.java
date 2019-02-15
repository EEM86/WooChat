package ua.woochat.app;

import java.io.*;
import java.net.Socket;

public class ConnectionImpl implements Connection, Runnable {
    private Socket socket;
    private ConnectionListener connectionListener;
    private OutputStream out;
    private InputStream in;
    private Thread thread;
    private boolean needToRun = true;

    public ConnectionImpl(Socket socket, ConnectionListener connectionListener) {
        try {
            this.socket = socket;
            this.connectionListener = connectionListener;
            out = socket.getOutputStream();
            in = socket.getInputStream();
            thread = new Thread(this);
            thread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void send(Message message) {
        try {
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        needToRun = false;
    }

    @Override
    public void run() {
        while (needToRun) {
            try {
                if (in.available() > 0) {
                    ObjectInputStream objIn = new ObjectInputStream(in);
                    Message msg = (Message) objIn.readObject();
                    connectionListener.sendToAll(msg);
                } else {
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
