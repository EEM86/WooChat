package ua.woochat.client.model;

public class ServerConnection {

    public ServerConnection(){}

    /**
     * method checks the existence of the user account
     * @param accountName user account name
     * @param accountPassword user password
     */
    public void userRequest (String accountName, String accountPassword){
        System.out.println("LoginForm user name: " + accountName);
        System.out.println("LoginForm user password : " + accountPassword);
    }

    /**
     * method sends a request to register a new user
     * @param accountName registration user name
     * @param accountPassword registration user password
     */
    public void registrationRequest (String accountName, String accountPassword){
        System.out.println("LoginForm registration user name: " + accountName);
        System.out.println("LoginForm registration user password : " + accountPassword);
    }
}
