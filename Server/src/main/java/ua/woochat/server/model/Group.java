package ua.woochat.server.model;

import ua.woochat.app.Connection;
import ua.woochat.app.User;
import ua.woochat.app.UsersAndGroups;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class Group implements UsersAndGroups {

    //private ArrayList<User> usersList = new ArrayList();   // скорее всего надо работать с объектами класса Connection
    public Set<Connection> usersList = new LinkedHashSet<>(); // tmp field

    private String groupName;
    private User adminGroup;
    private String groupID;


//    public Group(User adminGroup, int idGroup) {
//        this.adminGroup = adminGroup;
//        this.idGroup = idGroup;
//        addUser(adminGroup);
//    }

    public Group(String idGroup) {
        this.groupID = idGroup;
    }

    public Group(String groupName, String idGroup) {
        this.groupName = groupName;
        this.groupID = idGroup;
    }

    //    public void addUser (User user){
//        usersList.add(user);
//    }

    //public ArrayList<User> getUsersList() {
//        return usersList;
//    }
    public Set<Connection> getUsersList() {
        return usersList;
    }

    public void addUser (Connection connection){
        usersList.add(connection);
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupID() {
        return groupID;
    }
}
