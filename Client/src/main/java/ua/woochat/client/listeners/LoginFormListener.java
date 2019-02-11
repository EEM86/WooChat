package ua.woochat.client.listeners;

import ua.woochat.client.view.LoginForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * class defines the functionality associated with the events of pressing the buttons
 */

public class LoginFormListener implements ActionListener {

    LoginForm loginForm;

    public LoginFormListener(LoginForm loginForm){
        this.loginForm = loginForm;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e event associated with the push of a button
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        /**
         * event handling associated with pressing the button singInButton
         */
        if (e.getActionCommand().equals("signInButton")) {
            System.out.println("signInButton was pressed");
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

        /**
         * event handling associated with pressing the button newUserButton
         */

        if (e.getActionCommand().equals("create")) {
            System.out.println("create was pressed");
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
}
