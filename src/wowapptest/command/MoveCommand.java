package wowapptest.command;

import java.util.Date;

public class MoveCommand extends Command {
    private double lat, lon;

    public MoveCommand(Date time, String drone, double lat, double lon) {
        super(Type.MOVE, time, drone);
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() { return lat; }

    public double getLon() { return lon; }
}
