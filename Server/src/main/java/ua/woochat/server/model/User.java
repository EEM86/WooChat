package ua.woochat.server.model;

import java.util.ArrayList;

public class User {

    private String login;
    private String password;
    private enum Gender {
        MALE, FEMALE
    }
    private Gender gender;
    private boolean admin;
    private boolean ban;
    private ArrayList group = null;

    public User(String login, String password, Gender gender, boolean admin, boolean ban) {
        this.login = login;
        this.password = password;
        this.gender = gender;
        this.admin = admin;
        this.ban = ban;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
    } //admin по умолчанию false, помоему здесь надо присвоить true

    public boolean isBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }
}
