package ua.woochat.server.model;

import ua.woochat.app.Connection;
import ua.woochat.app.Message;
import ua.woochat.server.model.commands.Commands;
import ua.woochat.server.model.commands.admincmds.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines admin commands.
 */
public class AdminCommands {
    public final static String ADMINLOGIN = ConfigServer.getRootAdmin();
    public final static String STOPSERVER = "/stopServer";
    public final static String SET = "/set";
    public final static String RELAUNCHSERVER = "/relaunchServer";
    public final static String KICK = "/kick";
    public final static String BAN = "/ban";
    public final static String UNBAN = "/unban";
    public final static String HELP = "/help";

    private final static AdminCommands ADMIN_COMMANDS = new AdminCommands();
    private Map<String, Commands> adminCommandsMap = new HashMap<>();

    public static AdminCommands getInstance() {
        return ADMIN_COMMANDS;
    }

    public static Map<String, Commands> getCommandsMap() {
        return ADMIN_COMMANDS != null ? ADMIN_COMMANDS.adminCommandsMap : null;
    }

    private AdminCommands() {
        adminCommandsMap.put(KICK, new Kick());
        adminCommandsMap.put(STOPSERVER, new StopServer());
        adminCommandsMap.put(RELAUNCHSERVER, new RelaunchServer());
        adminCommandsMap.put(HELP, new Help());
        adminCommandsMap.put(BAN, new Ban());
        adminCommandsMap.put(UNBAN, new Unban());
        adminCommandsMap.put(SET, new SetCommand());
    }

    /**
     * Checks if it is admin connection
     * @param connect current connection
     * @return true if current connection is admin
     */
    public static boolean isConnectionAdmin(Connection connect) {
        return connect.getUser().getLogin().equals(ADMINLOGIN) ? true : false;
    }

    /**
     * Prints help command for admin
     * @param connection current connection
     * @param message current message object for communication with client - server
     */
    public static void printHelpAdmin(Connection connection, Message message) {
        Help help = new Help();
        help.execute(connection, message);
    }


}
