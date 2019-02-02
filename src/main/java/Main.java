import ua.woochat.client.Client;
import ua.woochat.server.Server;

public class Main {
    public static void main(String[] args) {
        Client cl = new Client();
        cl.sayHello();
        Server s1 = new Server();
        s1.sayHello();
    }
}
