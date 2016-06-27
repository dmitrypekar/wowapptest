package wowapptest;

import org.apache.log4j.Logger;
import wowapptest.command.Command;
import wowapptest.command.MoveCommand;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.DelayQueue;

public class Drone implements Runnable {
    private static final Logger logger = Logger.getLogger(Drone.class);
    private static final double SPEED = 300; // km/h
    private static final double STATION_DISTANCE = 0.350;

    private String id;
    private double lat, lon;

    private Date moveStarted, moveEnded;
    private double sourceLat, sourceLon;
    private double targetLat, targetLon;

    private String lastReportedStation;

    private DelayQueue<Command> commands = new DelayQueue<>();

    private boolean stopped;
    private Thread thread;
    private Dispatcher dispatcher;

    public Drone(String id, double lat, double lon) {
        this.id = id;

        this.lat = lat;
        this.lon = lon;

        this.moveStarted = new Date(Clock.time());
        this.moveEnded = moveStarted;
        this.sourceLat = lat;
        this.sourceLon = lat;
        this.targetLat = lat;
        this.targetLon = lon;
    }

    public String getId() { return id; }

    public double getLat() { return lat; }

    public double getLon() { return lon; }

    public double getSourceLat() { return sourceLat; }

    public double getSourceLon() { return sourceLon; }

    public double getTargetLat() { return targetLat; }

    public double getTargetLon() { return targetLon; }

    public boolean isMoving() { return moveEnded == null; }

    public double getMoveProgress() {
        double srcPosDistance = distance(sourceLat, sourceLon, lat, lon);
        double srcTgtDist = distance(sourceLat, sourceLon, targetLat, targetLon);
        return srcPosDistance / srcTgtDist;
    }

    public void start(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        thread = new Thread(this);
        thread.start();
    }

    public boolean isStopped() { return stopped; }

    public void stop() throws InterruptedException {
        thread.interrupt();
        thread.join();
        thread = null;
        dispatcher = null;
    }

    public void sendCommand(Command command) {
        commands.add(command);
    }

    @Override
    public void run() {
        for (;;) {
            try { execCommandIfAny(); }
            catch (Shutdown e) { break; }

            checkNearestStations();
            moveDroneIfRequired();

            try { Thread.sleep(500); }
            catch (InterruptedException e) { break; }
        }

        stopped = true;
    }

    private void execCommandIfAny() throws Shutdown {
        Command command = commands.poll();
        if (command == null) return;

        switch (command.getType()) {
            case MOVE:
                MoveCommand moveCommand = (MoveCommand) command;
                logger.info(String.format("Drone %s started moving to %.6f, %.6f", id, moveCommand.getLat(), moveCommand.getLon()));

                moveStarted = new Date(Clock.time());
                moveEnded = null;
                sourceLat = lat;
                sourceLon = lon;
                targetLat = moveCommand.getLat();
                targetLon = moveCommand.getLon();
                break;
            case SHUTDOWN:
                logger.info("Drone " + id + " is shutting down");
                throw new Shutdown();
            default:
                throw new UnsupportedOperationException("Unsupported command type " + command.getType());
        }
    }

    private void checkNearestStations() {
        Random random = new Random();

        for (Station station : dispatcher.getStations()) {
            double distance = distance(lat, lon, station.getLat(), station.getLon());

            if (distance < STATION_DISTANCE && !station.getId().equals(lastReportedStation)) {
                lastReportedStation = station.getId();
                Traffic traffic = Traffic.values()[random.nextInt(Traffic.values().length)];
                dispatcher.registerTraffic(id, station.getId(), Clock.time(), SPEED, traffic);
            }
        }
    }

    private void moveDroneIfRequired() {
        if (getMoveProgress() >= 1) {
            if (moveEnded == null) {
                logger.info(String.format("Drone %s reached target %.6f, %.6f", id, targetLat, targetLon));
                moveEnded = new Date(Clock.time());
            }
            return;
        }

        double d = SPEED * (Clock.time() - moveStarted.getTime()) / (1000 * 60 * 60);

        double passed = d / distance(sourceLat, sourceLon, targetLat, targetLon);
        if (passed > 1) passed = 1;

        double dLat = targetLat - sourceLat;
        double dLon = targetLon - sourceLon;
        lat = sourceLat + dLat * passed;
        lon = sourceLon + dLon * passed;
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344; // km
        return dist;
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private static class Shutdown extends RuntimeException {}
}
