package com.au_team11.aljuniorrangers;

import com.esri.core.geometry.Point;

/**
 * Created by JDSS on 2/25/16.
 */
public class ActionPoint {

    //Location on the map
    Point location;

    public ActionPoint(Point newLocation) {
        //set the location
        location = newLocation;
    }

    public Point getLocation() {
        return location;
    }

    public Boolean setLocation(Point newLocation) {
        if(newLocation != null) {
            location = newLocation;
            return true;
        }
        else {
            return false;
        }
    }

    //The action to do, defined in subclasses
    public void action() {
        //do nothing
    }
}
