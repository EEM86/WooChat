package ua.woochat.client;

import ua.woochat.client.view.LoginForm;
import ua.woochat.client.view.WindowImages;
import ua.woochat.client.view.WindowProperties;

/**
 * Main WooChat client application class
 * @autor Yevhen Yermolenko
 * @autor Mariia Romanovych
 * @autor Sergey Skidan
 * @version 0.9
*/

public class ClientApp {
    public static void main(String[] args) {
        WindowProperties properties = new WindowProperties();
        WindowImages images = new WindowImages();
        new LoginForm(properties, images);
    }
}
