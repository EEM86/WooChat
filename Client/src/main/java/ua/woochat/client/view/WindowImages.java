package ua.woochat.client.view;

import javax.swing.*;

public class WindowImages {

    private ImageIcon logoImage;
    private ImageIcon newUserLogo;
    private ImageIcon closeTabIcon;

    public WindowImages(){
        logoImage = new ImageIcon("Client/src/main/resources/logoImage.png");
        newUserLogo = new ImageIcon("Client/src/main/resources/newUserLogo.png");
        closeTabIcon = new ImageIcon("Client/src/main/resources/delete.png");
    }

    public ImageIcon getNewUserLogo() {
        return newUserLogo;
    }

    public ImageIcon getLogoImage() {
        return logoImage;
    }

    public ImageIcon getCloseTabIcon() {
        return closeTabIcon;
    }

}
