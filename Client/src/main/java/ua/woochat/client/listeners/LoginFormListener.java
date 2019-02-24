package ua.woochat.client.listeners;

import ua.woochat.app.Message;
import ua.woochat.client.model.ServerConnection;
import ua.woochat.client.view.LoginForm;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringWriter;

/**
 * class defines the functionality associated with the events of pressing the buttons
 */

public class LoginFormListener implements ActionListener {

    LoginForm loginForm;
    private ServerConnection serverConnection;

    public LoginFormListener(LoginForm loginForm){
        this.loginForm = loginForm;
        this.serverConnection = serverConnection;
        serverConnection = new ServerConnection(this);
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
            String account = loginForm.getUserName().getText();
            String password = loginForm.getUserPassword().getText();
            sendMessage(account, password, Message.SINGIN_TYPE);
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
            String account = loginForm.getNewLogin().getText();
            String password = loginForm.getNewPassword().getText();
            String passwordConfirm = loginForm.getNewConfirmPassword().getText();

            if (account.equals("")){
                System.out.println("Please enter account name!");
            }
            else{
                if (password.equals("") | passwordConfirm.equals("")){
                    System.out.println("Password must not be empty");
                }
                else {
                    if (!password.equals(passwordConfirm)){
                        System.out.println("Passwords do not match");
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

    private void sendMessage(String account, String password, int type) {
        Message message = new Message(account, password, type);
        try {
            String str = marshalling(message);
            serverConnection.sendToServer(str);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private String marshalling (Message message) throws JAXBException {
        StringWriter writer = new StringWriter();
        //создание объекта Marshaller, который выполняет сериализацию
        JAXBContext context = JAXBContext.newInstance(Message.class);
        Marshaller marshaller = context.createMarshaller();
        // сама сериализация
        marshaller.marshal(message, writer);

        //преобразовываем в строку все записанное в StringWriter
        String result = writer.toString();
        System.out.println(result);
        return result;
    }

}
