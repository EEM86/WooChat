package ua.woochat.app;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

@XmlType(propOrder = { "login", "message", "time" }, name = "historyMessage")
@XmlRootElement
public class HistoryMessage {
    @XmlElement
    private String login;
    @XmlElement
    private String message;
    @XmlElement
    private Date time;

    public HistoryMessage() {
    }

    public HistoryMessage(String login, String message){
        this.login = login;
        this.message = message;
        this.time = new Date();
    }

    public String getLogin() {
        return this.login;
    }

    public String getMessage() {
        return this.message;
    }

    public Date getTime() {
        return time;
    }
}
