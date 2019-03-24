package ua.woochat.server.model;

import org.apache.log4j.Logger;
import ua.woochat.app.Message;
import ua.woochat.server.model.commands.Commands;
import ua.woochat.server.model.commands.admincmds.*;

import java.util.HashMap;
import java.util.Map;

public class AdminCommands {
    private final static Logger logger = Logger.getLogger(AdminCommands.class);

    public final static String STOPSERVER = "/stopServer";
//    public final static String SETPORTCONNECTION = "/set portConnection";
////    public final static String SETPORTCHATTING = "/set portChatting";
////    public final static String SETTIMEOUT = "/set timeout";
    public final static String SET = "/set";
    public final static String RELAUNCHSERVER = "/relaunchServer";
    public final static String KICK = "/kick";
    public final static String BAN = "/ban";
    public final static String UNBAN = "/unban";
    public final static String HELP = "/help";
    public final static String ADMINLOGIN = ConfigServer.getRootAdmin();

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


}
