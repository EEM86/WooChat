package ua.woochat.client.view;

import javax.swing.*;

public class WindowImages {

    private ImageIcon logoImage;
    private ImageIcon newUserLogo;

    public WindowImages(){
        logoImage = new ImageIcon("Client/src/main/resources/logoImage.png");
        newUserLogo = new ImageIcon("Client/src/main/resources/newUserLogo.png");
    }

    public ImageIcon getNewUserLogo() {
        return newUserLogo;
    }

    public ImageIcon getLogoImage() {
        return logoImage;
    }
}
