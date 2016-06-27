package wowapptest.command;

import java.util.Date;

public class ShutdownCommand extends Command {
    public ShutdownCommand(Date time, String drone) {
        super(Type.SHUTDOWN, time, drone);
    }
}
