package com.au_team11.aljuniorrangers;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by JDSS on 1/30/16.
 */
public class TrailWalk extends ParkActivity {

    ArrayList<Coordinate> waypoints;
    ArrayList<Boolean> waypointsVisited;
    private double totalDistance;
    private Coordinate currentPosition;
    public Bitmap map;
    public Coordinate topLeftMap;
    public Coordinate bottomRightMap;

    public TrailWalk(Boolean alreadyCompleted, ArrayList<Coordinate> newWaypoints, Bitmap newMap) {
        super(alreadyCompleted);
        waypoints = newWaypoints;
        totalDistance = calculateDistance(waypoints);
        map = newMap;
    }

    public TrailWalk(Boolean alreadyCompleted, ArrayList<Coordinate> newWaypoints, ArrayList<Boolean> newWaypointsVisited, Bitmap newMap) {
        super(alreadyCompleted);
        waypoints = newWaypoints;
        waypointsVisited = newWaypointsVisited;
        totalDistance = calculateDistance(waypoints);
        map = newMap;
    }

    public ArrayList<Coordinate> getWaypoints() {
        return waypoints;
    }

    public ArrayList<Boolean> getWaypointsVisited() {
        return waypointsVisited;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Coordinate currentPosition) {
        this.currentPosition = currentPosition;
    }

    //finds the distance between two coordinates
    public double calculateDistance(Coordinate waypoint0, Coordinate waypoint1) {

        //distance is sqrt((x2 - x1)^2 + (y2 - y1)^2)
        double distance = Math.sqrt(
                                    Math.pow(
                                        (waypoint0.getLatitude() - waypoint1.getLatitude())
                                        , 2)
                                  + Math.pow(
                                        (waypoint0.getLongitude() - waypoint1.getLongitude())
                                        , 2));

        return distance;
    }

    // Sums the distances between each consecutive pair of points in waypoints
    public double calculateDistance(ArrayList<Coordinate> waypoints) {

        double distance = 0;
        for (int i = 1; i < waypoints.size(); i++) {
            //distance is sqrt((x2 - x1)^2 + (y2 - y1)^2)
            distance += Math.sqrt(
                            Math.pow(
                                (waypoints.get(i).getLatitude() - waypoints.get(i-1).getLatitude())
                                , 2)
                          + Math.pow(
                                (waypoints.get(i).getLongitude() - waypoints.get(i-1).getLongitude())
                                , 2));
        }
        return distance;
    }
}
