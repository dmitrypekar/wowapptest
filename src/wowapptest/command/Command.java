package wowapptest.command;

import wowapptest.Clock;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class Command implements Delayed {
    private Type type;
    private Date time;
    private String drone;

    public Command(Type type, Date time, String drone) {
        this.type = type;
        this.time = time;
        this.drone = drone;
    }

    public Type getType() { return type; }

    public Date getTime() { return time; }

    public String getDrone() { return drone; }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(time.getTime() - Clock.time(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed d) {
        Command other = (Command) d;
        return time.compareTo(other.time);
    }

    public enum Type {
        MOVE,
        SHUTDOWN
    }
}
