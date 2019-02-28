package ua.woochat.server.model;

import ua.woochat.app.User;
import ua.woochat.app.UsersAndGroups;

import java.util.ArrayList;

public class Group implements UsersAndGroups {

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
