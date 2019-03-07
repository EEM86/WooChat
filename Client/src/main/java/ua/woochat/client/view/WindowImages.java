package ua.woochat.client.view;

import javax.swing.*;

public class WindowImages {

    private ImageIcon logoImage;
    private ImageIcon newUserLogo;
    private ImageIcon closeTabIcon;
    private ImageIcon line;

    public WindowImages(){
        logoImage = new ImageIcon("Client/src/main/resources/logoImage.png");
        newUserLogo = new ImageIcon("Client/src/main/resources/newUserLogo.png");
        closeTabIcon = new ImageIcon("Client/src/main/resources/delete.png");
        line = new ImageIcon("Client/src/main/resources/line.png");
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
