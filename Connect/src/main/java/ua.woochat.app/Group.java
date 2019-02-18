package ua.woochat.app;

import java.util.ArrayList;

public class Group {

    private ArrayList<User> usersList = new ArrayList();
    private User adminGroup;
    private int idGroup;

    {
        idGroup++;
    }

    public Group(User adminGroup, int idGroup) {
        this.adminGroup = adminGroup;
        this.idGroup = idGroup;
        addUser(adminGroup);
    }

    public void addUser (User user){
        usersList.add(user);
    }

    public ArrayList<User> getUsersList() {
        return usersList;
    }
}
