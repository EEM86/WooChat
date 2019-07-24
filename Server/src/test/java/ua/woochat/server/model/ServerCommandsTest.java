package ua.woochat.server.model;

import org.junit.Test;
import ua.woochat.app.Message;
import ua.woochat.server.model.commands.Commands;
import ua.woochat.server.model.commands.RegisterUser;
import ua.woochat.server.model.commands.admincmds.Kick;

import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class ServerCommandsTest {

    @Test
    public void checkCommandsMap() {
        Map<Integer, Commands> commandsMap = ServerCommands.getCommandsMap();
        assertThat(commandsMap.get(888), is(nullValue()));
        assertThat(commandsMap.size(), is(11));
        assertThat(commandsMap.get(Message.REGISTER_TYPE), is(instanceOf(RegisterUser.class)));
        assertThat(commandsMap.get(Message.KICK_TYPE), is(instanceOf(Kick.class)));
    }
}