package ua.woochat.app;

import org.apache.log4j.Logger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@XmlRootElement
public class User implements UsersAndGroups {
    @XmlElement
    private int id;
    @XmlElement
    private String login;
    @XmlElement
    private String password;
    private final static Logger logger = Logger.getLogger(User.class);

    private boolean admin;
    private boolean isBanned;
    private long lastActivity;
    private long timetoUnban;

    @XmlElementWrapper(name="ListGroup", nillable = true)
    @XmlElement(name="group")
    public Set<String> groups = new LinkedHashSet<>();

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.id = login.hashCode();
    }

    public User() {
    }

    /**
     * Method saves user in XML file
     */
    public void saveUser() {
        HandleXml handleXml = new HandleXml();
        File file = new File(  "User" + File.separator + this.getId() + ".xml");

        File directory = new File("User");
        if (!directory.exists()){
            directory.mkdir();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            logger.error("File has not been created ", e);
        }
        try {
            FileOutputStream stream = new FileOutputStream(file);
            handleXml.marshalling(User.class, this, stream);
        } catch (FileNotFoundException e) {
            logger.error("File not found exceptions ", e);
        }

    }

    public int getId() {
        return id;
    }


    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
        saveUser();
    }

    public void addGroup(String groupID) {
        groups.add(groupID);
        saveUser();
    }

    public void removeGroup(String groupID) {
        groups.remove(groupID);
        saveUser();
    }


    public Set getGroups() {
        return groups;
    }

    @XmlElementWrapper(nillable = true)
    public void setGroups(Set<String> groups) {
        this.groups = groups;
        saveUser();
    }

    public boolean isBan() {
        return isBanned;
    }

    public void setBan(boolean ban) {
        this.isBanned = ban;
        saveUser();
    }

    /**
     * Sets ban interval for user.
     * @param interval in minutes;
     */
    public void setBanInterval(int interval) {
        int minute = 60000;
        int minutesInMillisecs = minute;
        setBan(true);
        timetoUnban = System.currentTimeMillis() + interval * minutesInMillisecs;
        saveUser();
    }

    public void unban() {
        timetoUnban = 0;
        setBan(false);
        //saveUser();
        saveUser();
    }

    public boolean readyForUnban() {
        if (timetoUnban <= System.currentTimeMillis()) {
            unban();
            return true;
        } else return false;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
        saveUser();
    }

    /**
     * Method user to String
     * @return String for user
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                //", gender=" + gender +
                ", admin=" + admin +
                ", isBanned=" + isBanned +
                ", groups=" + groups +
                '}';
    }
}
