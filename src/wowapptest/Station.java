package wowapptest;

public class Station {
    private String id;
    private double lat, lon;

    public Station(String id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() { return id; }

    public double getLat() { return lat; }

    public double getLon() { return lon; }
}
