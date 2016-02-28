package com.au_team11.aljuniorrangers;

import android.app.Activity;
import android.util.Log;

import com.esri.core.geometry.Point;

/**
 * Created by JDSS on 2/25/16.
 */
public class ActionPoint {

    //main activity
    Activity activity;

    //text to display about point
    String text;

    //Location on the map
    Point location;

    public ActionPoint(Activity newActivity, Point newLocation, String newText) {
        //set the activity
        activity = newActivity;
        //set the location
        location = newLocation;
        //set the text
        text = newText;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    //The action to do, defined in subclasses
    public void action() {
        Log.i("ActionPoint.action", "ActionPoint was clicked");
    }
}
