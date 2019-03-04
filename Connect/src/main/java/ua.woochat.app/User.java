package ua.woochat.app;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

@XmlRootElement
public class User implements UsersAndGroups {
    @XmlElement
    private int id;
    @XmlElement
    public String login; //change to private later
    @XmlElement
    private String password;

    private enum Gender {
        MALE, FEMALE
    }

    private Gender gender;
    private boolean admin;
    private boolean isBanned;
    public Set<String> groups = new LinkedHashSet<>();

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.id = login.hashCode();
    }


    public User() {
    }

    public void saveUser() {
        HandleXml handleXml = new HandleXml();
        String path = new File("").getAbsolutePath();
        File file = new File(path + "/Server/src/main/resources/User/" + this.getId() + ".xml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream stream = new FileOutputStream(file);
            handleXml.marshalling(User.class, this, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
        admin = true;  //admin по умолчанию false, помоему здесь надо присвоить true
    }

    public boolean isBan() {
        return isBanned;
    }

    public void setBan(boolean ban) {
        this.isBanned = isBanned;
    }

    public void addGroup(String groupID) {
        groups.add(groupID);
    }

    public Set getGroups() {
        return groups;
    }
}
