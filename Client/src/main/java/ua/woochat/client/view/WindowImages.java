package ua.woochat.client.view;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Class describe a ImageIcon resources
 */
public class WindowImages {
    final static Logger logger = Logger.getLogger(WindowImages.class);
    private ImageIcon logoImage;
    private ImageIcon newUserLogo;
    private ImageIcon closeTabIcon;
    private ImageIcon line;
    private ImageIcon envelope;
    private ImageIcon logo;

    public WindowImages() {
        try {
        logoImage = new ImageIcon(getClass().getClassLoader().getResource("logoImage.png"));
        newUserLogo = new ImageIcon(getClass().getClassLoader().getResource("newUserLogo.png"));
        closeTabIcon = new ImageIcon(ClassLoader.getSystemClassLoader().getResource("delete.png"));
        line = new ImageIcon(ClassLoader.getSystemClassLoader().getResource("line.png"));
        envelope = new ImageIcon(ClassLoader.getSystemClassLoader().getResource("envelope.png"));
        logo = new ImageIcon(ClassLoader.getSystemClassLoader().getResource("logo.png"));
        //logoImage = new ImageIcon("Client/src/main/resources/logoImage.png");
//        newUserLogo = new ImageIcon("Client/src/main/resources/newUserLogo.png");
//        closeTabIcon = new ImageIcon("Client/src/main/resources/delete.png");
//        line = new ImageIcon("Client/src/main/resources/line.png");
//        envelope = new ImageIcon("Client/src/main/resources/envelope.png");
//        logo = new ImageIcon("Client/src/main/resources/logo.png");
        } catch (Exception e) {
            logger.error("Error" + e);
        }
    }

     ImageIcon getEnvelope() {
        return envelope;
    }

    ImageIcon getLogo() {
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
