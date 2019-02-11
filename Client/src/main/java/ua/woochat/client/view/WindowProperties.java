package ua.woochat.client.view;

import java.awt.*;

public class WindowProperties {

    private Color bgColor;
    private Color textColor;
    private Color btnColor;

    public WindowProperties(){
        bgColor = new Color(130,85,150);
        textColor = new Color(255,255,255);
        btnColor = new Color(255, 255, 255);
    }

    public Color getBgColor() {
        return bgColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getBtnColor() {
        return btnColor;
    }
}
