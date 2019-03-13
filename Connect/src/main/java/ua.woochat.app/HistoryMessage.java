package ua.woochat.app;

import javax.xml.bind.annotation.*;
import java.util.Date;


@XmlType(propOrder = { "login", "message", "time" }, name = "historyMessage")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
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

    /**
     * Method gets user login of historical message
     * @return this.login of historical message
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * Method gets message of historical message
     * @return this.message of historical message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Method gets times of historical message
     * @return time of historical message
     */
    public Date getTime() {
        return time;
    }

    /**
     * Method historical message to String
     * @return String for historical message
     */
    @Override
    public String toString() {
        return "HistoryMessage{" +
                "login='" + login + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                '}';
    }
}
