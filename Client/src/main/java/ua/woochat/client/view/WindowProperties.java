package ua.woochat.client.view;

import java.awt.*;

/**
 * Class describe a color properties
 */
public class WindowProperties {

    private Color bgColor;
    private Color textColor;
    private Color btnColor;
    private Color chatBackColor;
    private Color labelTextColor;
    private Color userListColor;

    public WindowProperties(){
        bgColor = new Color(130,85,150);
        textColor = new Color(255,255,255);
        btnColor = new Color(255, 255, 255);
        chatBackColor = new Color(58, 37, 75);
        labelTextColor  = new Color(255, 255, 255);
        userListColor  = new Color(230, 196, 221);
    }

    Color getBgColor() {
        return bgColor;
    }

    Color getTextColor() {
        return textColor;
    }

    Color getBtnColor() {
        return btnColor;
    }

    Color getChatBackColor() {
        return chatBackColor;
    }

    Color getLabelTextColor() {
        return labelTextColor;
    }

    Color getUserListColor() {
        return userListColor;
    }
}
