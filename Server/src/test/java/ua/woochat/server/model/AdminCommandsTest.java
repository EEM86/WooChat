package ua.woochat.server.model;

import org.junit.Test;
import ua.woochat.server.model.commands.Commands;
import ua.woochat.server.model.commands.admincmds.Help;
import ua.woochat.server.model.commands.admincmds.Kick;

import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AdminCommandsTest {

    @Test
    public void getCommandsMap() {
        ConfigServer.getConfigServer();
        Map<String, Commands> commandsMap = AdminCommands.getCommandsMap();
        assertThat(commandsMap.size(), is(7));
        assertThat(commandsMap.get(AdminCommands.KICK), is(instanceOf(Kick.class)));
        assertThat(commandsMap.get(AdminCommands.HELP), is(instanceOf(Help.class)));
    }
}