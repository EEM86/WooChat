package ua.woochat.server.model;

import java.util.ArrayList;

public class Group {

    private ArrayList<User> userList = new ArrayList();
    private User adminGroup;
    private int idGroup;

    {
        idGroup ++;
    }

    public Group(User adminGroup, int idGroup) {
        this.adminGroup = adminGroup;
        this.idGroup = idGroup;
        addUser(adminGroup);
    }

    public void addUser (User user){
        userList.add(user);
    }

    public ArrayList<User> getUserList() {
        return userList;
    }
}
