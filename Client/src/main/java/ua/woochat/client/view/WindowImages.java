package ua.woochat.client.view;

import javax.swing.*;

public class WindowImages {

    private ImageIcon logoImage;
    private ImageIcon newUserLogo;
    private ImageIcon closeTabIcon;
    private ImageIcon line;
    private ImageIcon envelope;
    private ImageIcon logo;

    public WindowImages(){
        logoImage = new ImageIcon("Client/src/main/resources/logoImage.png");
        newUserLogo = new ImageIcon("Client/src/main/resources/newUserLogo.png");
        closeTabIcon = new ImageIcon("Client/src/main/resources/delete.png");
        line = new ImageIcon("Client/src/main/resources/line.png");
        envelope = new ImageIcon("Client/src/main/resources/envelope.png");
        logo = new ImageIcon("Client/src/main/resources/logo.png");
    }

     ImageIcon getEnvelope() {
        return envelope;
    }

    public ImageIcon getLogo() {
        return logo;
    }

    ImageIcon getNewUserLogo() {
        return newUserLogo;
    }

     ImageIcon getLogoImage() {
        return logoImage;
    }

     ImageIcon getCloseTabIcon() { return closeTabIcon; }

     ImageIcon getLine() { return line; }
}
