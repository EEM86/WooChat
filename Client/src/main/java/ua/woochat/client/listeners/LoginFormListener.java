package ua.woochat.client.listeners;

import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.client.model.ServerConnection;
import ua.woochat.client.view.LoginForm;
import ua.woochat.client.view.MessageView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * class defines the functionality associated with the events of pressing the buttons
 */
public class LoginFormListener implements ActionListener {

    private LoginForm loginForm;
    private ServerConnection serverConnection;
    private HandleXml handleXml = new HandleXml();

    public LoginFormListener(LoginForm loginForm){
        serverConnection = new ServerConnection(this);
        this.loginForm = loginForm;
    }

    /**
     * Invoked when an action occurs.
     * @param e event associated with the push of a button
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        /**
         * event handling associated with pressing the button singInButton
         */
        if (e.getActionCommand().equals("signInButton")) {
            signIn();
        }

        /**
         * event handling associated with pressing the button registerButton
         */
        
        if (e.getActionCommand().equals("registerButton")) {
            loginForm.getLoginWindow().setVisible(false);
            loginForm.getLoginWindow().setTitle("New account");
            loginForm.getLoginWindow().getContentPane().remove(loginForm.getLoginPanel());
            loginForm.getLoginWindow().getContentPane().add(loginForm.getRegistrationPanel());
            loginForm.getLoginWindow().setVisible(true);
        }

        if (e.getActionCommand().equals("passwordEnterPressed")) {
            signIn();
        }

        /**
         * event handling associated with pressing the button newUserButton
         */

        if (e.getActionCommand().equals("create")) {
            String account = loginForm.getNewLogin().getText();
            String password = loginForm.getNewPassword().getText();
            String passwordConfirm = loginForm.getNewConfirmPassword().getText();

            if (("").equals(account)){
                new MessageView("Please enter account name!", loginForm.getLoginWindow(),false);
            }
            else{
                if (("".equals(password)) | ("".equals("passwordConfirm"))){
                    new MessageView("Password must not be empty", loginForm.getLoginWindow(),false);
                }
                else {
                    if (!password.equals(passwordConfirm)){
                        new MessageView("Passwords do not match", loginForm.getLoginWindow(),false);
                    }
                    else{
                        sendMessage(account, password, Message.REGISTER_TYPE);
                    }
                }
            }
        }

        /**
         * event handling associated with pressing the button cancelNewUserButton
         */
        if (e.getActionCommand().equals("cancel")) {
            loginForm.getLoginWindow().setVisible(false);
            loginForm.getLoginWindow().setTitle("Login chat");
            loginForm.getLoginWindow().getContentPane().remove(loginForm.getRegistrationPanel());
            loginForm.getLoginWindow().getContentPane().add(loginForm.getLoginPanel());
            loginForm.getLoginWindow().setVisible(true);
        }

    }

    /**
     * Method sends data to server
     */
    private void signIn() {
        String account = loginForm.getUserName().getText();
        String password = loginForm.getUserPassword().getText();

        if (("".equals(account)) || ("".equals(password))){
            new MessageView("Login or password must not be empty", loginForm.getLoginWindow(),false);
        }else{
            sendMessage(account, password, Message.SIGNIN_TYPE);
        }
    }

    /**
     * Method send user login and password to server
     * @param account user login
     * @param password user password
     * @param type type of message
     */
    private void sendMessage(String account, String password, int type) {
        Message message = new Message(account, password, type);
        String str = handleXml.marshallingWriter(Message.class, message);

        try {
            serverConnection.sendToServer(str);
        }catch (NullPointerException e){
            new MessageView("Server is not available", loginForm.getLoginWindow(),false);
        }
    }
    public LoginForm getLoginForm() {
        return loginForm;
    }
}
