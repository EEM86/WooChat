package ua.woochat.server.model;

import ua.woochat.app.Message;
import ua.woochat.server.model.commands.*;
import ua.woochat.server.model.commands.admincmds.Kick;

import java.util.HashMap;
import java.util.Map;

/**
 * This class creates server commands map.
 */
public class ServerCommands {
    private final static ServerCommands CHATTING_COMMANDS = new ServerCommands();
    private Map<Integer, Commands> chatCommandsMap = new HashMap<>();

    public static ServerCommands getInstance() {
        return CHATTING_COMMANDS;
    }

    public static Map<Integer, Commands> getCommandsMap() {
        return CHATTING_COMMANDS != null ? CHATTING_COMMANDS.chatCommandsMap : null;
    }

    private ServerCommands() {
        chatCommandsMap.put(Message.REGISTER_TYPE, new RegisterUser());
        chatCommandsMap.put(Message.SIGNIN_TYPE, new SignInUser());
        chatCommandsMap.put(Message.CHATTING_TYPE, new Chatting());
        chatCommandsMap.put(Message.UPDATE_USERS_TYPE, new UpdateUsersList());
        chatCommandsMap.put(Message.PRIVATE_CHAT_TYPE, new PrivateChat());
        chatCommandsMap.put(Message.PRIVATE_GROUP_TYPE, new PrivateGroup());
        chatCommandsMap.put(Message.UNIQUE_ONLINE_USERS_TYPE, new UniqueOnlineUsers());
        chatCommandsMap.put(Message.LEAVE_GROUP_TYPE, new LeavePrivateGroup());
        chatCommandsMap.put(Message.EXIT_TYPE, new Exit());
        chatCommandsMap.put(Message.PING_TYPE, new Ping());
        chatCommandsMap.put(Message.KICK_TYPE, new Kick());
    }
}
