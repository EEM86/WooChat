package ua.woochat.app;

import java.io.Serializable;

public interface Message extends Serializable {
    public int CLOSE_TYPE = 0; // content type for quit
    public int CONTENT_TYPE = 1; // content type for chatting

    public String getNickName();
    public String getContent();
    public int getType();
}
