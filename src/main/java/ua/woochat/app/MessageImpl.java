package ua.woochat.app;

public class MessageImpl implements Message {
    private final String nickName;
    private final String content;
    private final int type;

    public MessageImpl(String nickName, String content, int type) {
        this.nickName = nickName;
        this.content = content;
        this.type = type;
    }

    @Override
    public String getNickName() {
        return nickName;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "" + nickName + ": " + content;
    }
}
