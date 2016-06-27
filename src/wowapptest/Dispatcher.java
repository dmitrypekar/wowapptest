package wowapptest;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;
import wowapptest.command.Command;
import wowapptest.command.MoveCommand;
import wowapptest.command.ShutdownCommand;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Dispatcher {
    private static final Logger logger = Logger.getLogger(Dispatcher.class);

    private List<Station> stations = new ArrayList<>();
    private List<Drone> drones = new ArrayList<>();
    private List<Command> commands = new ArrayList<>();

    public List<Drone> getDrones() {
        return Collections.unmodifiableList(drones);
    }

    public Drone getDrone(String id) {
        for (Drone drone : drones)
            if (drone.getId().equals(id)) return drone;
        return null;
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }

    public void load() throws IOException {
        loadStations();
        loadDrones();
        loadCommands();
    }

    private void loadStations() throws IOException {
        try (CSVReader reader = new CSVReader(new FileReader("stations.csv"))) {
            reader.readNext(); // skip header

            for (String[] line = reader.readNext(); line != null; line = reader.readNext()) {
                // station,lat,lon
                String id = line[0];
                double lat = Double.parseDouble(line[1]);
                double lon = Double.parseDouble(line[2]);
                stations.add(new Station(id, lat, lon));
            }
        }
    }

    private void loadDrones() throws IOException {
        try (CSVReader reader = new CSVReader(new FileReader("drones.csv"))) {
            reader.readNext(); // skip header

            for (String[] line = reader.readNext(); line != null; line = reader.readNext()) {
                // id,lat,lon
                String id = line[0];
                double lat = Double.parseDouble(line[1]);
                double lon = Double.parseDouble(line[2]);
                drones.add(new Drone(id, lat, lon));
            }
        }
    }

    private void loadCommands() throws IOException {
        try (CSVReader reader = new CSVReader(new FileReader("commands.csv"))) {
            reader.readNext(); // skip header

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            for (String[] line = reader.readNext(); line != null; line = reader.readNext()) {
                // id,time,lat,lon
                String id = line[0];

                Date time;
                try { time = dateFormat.parse(line[1]); }
                catch (ParseException e) { throw new IOException(e); }

                Command.Type type = Command.Type.valueOf(line[2].toUpperCase());
                switch (type) {
                    case MOVE:
                        double lat = Double.parseDouble(line[3]);
                        double lon = Double.parseDouble(line[4]);
                        commands.add(new MoveCommand(time, id, lat, lon));
                        break;
                    case SHUTDOWN:
                        commands.add(new ShutdownCommand(time, id));
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported command type " + type);
                }
            }
        }
    }

    public void start() {
        for (Drone drone : drones)
            drone.start(this);
    }

    public void run() throws InterruptedException {
        if (!commands.isEmpty()) {
            Command command = commands.get(0);
            Clock.init(command.getTime());
        }

        for (Command command : commands) {
            Drone drone = getDrone(command.getDrone());
            if (drone != null) drone.sendCommand(command);
        }

        for (;;) {
            boolean anyRunning = false;
            for (Drone drone : getDrones()) {
                anyRunning |= !drone.isStopped();
                String moving = !drone.isMoving() ? "- waiting" : String.format("- moving (%.2f%%) to %.6f, %.6f", drone.getMoveProgress() * 100, drone.getTargetLat(), drone.getTargetLon());
                logger.info(String.format("Drone %s position %.6f, %.6f %s", drone.getId(), drone.getLat(), drone.getLon(), moving));
            }

            if (!anyRunning) break;
            Thread.sleep(5000);
        }
    }

    public void stop() throws InterruptedException {
        for (Drone drone : drones)
            drone.stop();
    }

    public void registerTraffic(String drone, String station, long time, double speed, Traffic traffic) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        logger.info(String.format("Registered traffic at station %s from drone %s, time:%s, speed:%.2f, traffic:%s", station, drone, dateFormat.format(new Date(time)), speed, "" + traffic));
    }
}
