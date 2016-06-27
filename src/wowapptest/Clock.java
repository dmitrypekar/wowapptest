package wowapptest;

import java.util.Date;

public class Clock {
    private static Date baseTime = new Date();
    private static Date started = new Date();

    public static void init(Date baseTime) {
        Clock.baseTime = baseTime;
        started = new Date();
    }

    public static long time() {
        return System.currentTimeMillis() - started.getTime() + baseTime.getTime();
    }
}
