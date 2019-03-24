package ua.woochat.client.view;

import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Class describe a ImageIcon resources
 */
public class WindowImages {
    private final static Logger logger = Logger.getLogger(WindowImages.class);
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
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }

    public ImageIcon getEnvelope() {
        return envelope;
    }

    public ImageIcon getLogo() {
        return logo;
    }

    public ImageIcon getNewUserLogo() {
        return newUserLogo;
    }

    public ImageIcon getLogoImage() {
        return logoImage;
    }

    public ImageIcon getCloseTabIcon() { return closeTabIcon; }

    public ImageIcon getLine() { return line; }
}
