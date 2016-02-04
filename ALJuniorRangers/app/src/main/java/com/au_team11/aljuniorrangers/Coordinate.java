package com.au_team11.aljuniorrangers;

/**
 * Created by JDSS on 1/30/16.
 */
public class Coordinate {

    private double latitude;
    private double longitude;

    public Coordinate(double newLat, double newLon) {
        latitude = newLat;
        longitude = newLon;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
