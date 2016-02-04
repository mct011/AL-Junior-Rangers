package com.au_team11.aljuniorrangers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by JDSS on 1/30/16.
 */
public class TrailWalkView extends View {

    public static final double EPSILON = 0.00001;

    public static final int WAYPOINT_RADIUS_DP   = 4;
    public static final int TRAIL_WIDTH_DP       = 3;
    public static final int LOCATION_RADIUS_DP   = 4;
    public static final double PADDING_MULT      = 0.90;

    TrailWalk trailWalk;

    Paint waypointPaint;
    Paint trailPaint;
    Paint locationPaint;

    //the height/width of the screen
    double canvasHeightWidthRatio;
    //the number of pixels per degree based on this zoom level
    double pxDegRatio;

    //the percentage of screen that the points will occupy
    //keeps points from straddling the edge of the screen
    double paddingMult;

    //edges of the screen defined in coordinate degrees (DD.DDDDD)
    double leftScreenLon;
    double rightScreenLon;
    double topScreenLat;
    double bottomScreenLat;

    //the section of the map picture to show on the screen
    Rect mapSrcRect;
    //the section of screen to show the map on
    Rect mapDestRect;

    double dpPerDeg;

    public TrailWalkView(Context context, AttributeSet attrs) {
        super(context, attrs);

        waypointPaint = new Paint();
        trailPaint = new Paint();
        locationPaint = new Paint();

        waypointPaint.setARGB (255, 0, 124, 0);
        trailPaint.setARGB(255, 0, 124, 0);
        trailPaint.setStrokeWidth((float) dpToPx(TRAIL_WIDTH_DP));
        locationPaint.setARGB(255, 255, 0, 0);

        //how much of the screen the map will occupy at its widest or tallest. It allows for padding
        paddingMult = 0.90;

        //should always trigger setEdges on new object
        canvasHeightWidthRatio = 0;

    }

    public void setTrailWalk(TrailWalk trailWalk) {
        this.trailWalk = trailWalk;
    }

    protected void onDraw(Canvas canvas) {
        Log.i("TrailWalkView", "onDraw");
        super.onDraw(canvas);

        //if screen dimensions have changed
        if (Math.abs(canvasHeightWidthRatio - ((double) canvas.getHeight() / (double) canvas.getWidth())) > EPSILON) {
            //recalculate the lat, lon lines of the screen edges
            setEdges(trailWalk.getWaypoints(), canvas);
            //recalculate the map's Rects to keep it to scale
            scaleMap(canvas);
        }

        //draw map
        canvas.drawBitmap(trailWalk.map, mapSrcRect, mapDestRect, new Paint());

        //draw waypoints
        for (int i = 0; i < trailWalk.getWaypoints().size(); i++) {
            canvas.drawCircle((float) ((trailWalk.getWaypoints().get(i).getLongitude() - leftScreenLon) * pxDegRatio),
                              (float) ((topScreenLat - trailWalk.getWaypoints().get(i).getLatitude()) * pxDegRatio),
                              (float) dpToPx(WAYPOINT_RADIUS_DP),
                              waypointPaint);
            //draw a line between the current point and its previous point
            if (i > 0) {
                canvas.drawLine((float) ((trailWalk.getWaypoints().get(i).getLongitude() - leftScreenLon) * pxDegRatio),
                                (float) ((topScreenLat - trailWalk.getWaypoints().get(i).getLatitude()) * pxDegRatio),
                                (float) ((trailWalk.getWaypoints().get(i-1).getLongitude() - leftScreenLon) * pxDegRatio),
                                (float) ((topScreenLat - trailWalk.getWaypoints().get(i-1).getLatitude()) * pxDegRatio),
                                trailPaint);
            }
        }

        //draw an icon on the current location
        canvas.drawCircle((float) ((trailWalk.getCurrentPosition().getLongitude() - leftScreenLon) * pxDegRatio),
                          (float) ((topScreenLat - trailWalk.getCurrentPosition().getLatitude()) * pxDegRatio),
                          (float) dpToPx(LOCATION_RADIUS_DP),
                          locationPaint);
    }

    private void setEdges(ArrayList<Coordinate> points, Canvas canvas) {

        //find lat and lon range of the points
        double leftMax = points.get(0).getLongitude();
        double rightMax = points.get(0).getLongitude();
        double topMax = points.get(0).getLatitude();
        double bottomMax = points.get(0).getLatitude();

        //NOTE: Since this was made for Alabama, there is no checking for
        //      boundary lines such as the equator or prime meridian
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).getLongitude() < leftMax)
                leftMax = points.get(i).getLongitude();
            if (points.get(i).getLongitude() > rightMax)
                rightMax = points.get(i).getLongitude();
            if (points.get(i).getLatitude() > topMax)
                topMax = points.get(i).getLatitude();
            if (points.get(i).getLatitude() < bottomMax)
                bottomMax = points.get(i).getLatitude();
        }

        //find the height/width ratio of the waypoints
        double pointsRatio = (topMax - bottomMax) / (rightMax - leftMax);
        //find the height/width ratio of the canvas
        canvasHeightWidthRatio = ((double) canvas.getHeight()) / ((double) canvas.getWidth());
        Log.i("TrailWalkView", "cHWR: " + canvasHeightWidthRatio);

        //if points wider than screen
        if (canvasHeightWidthRatio > pointsRatio) {
            //use x coord for scale factor
            pxDegRatio = paddingMult * ((double) canvas.getWidth()) / (rightMax - leftMax);
        }
        //else screen wider than points
        else {
            //use y coord for scale factor
            pxDegRatio = paddingMult * ((double) canvas.getHeight()) / (topMax - bottomMax);
        }

        //set edges according to lat, lon degree range
        //an edge's degree value = the farthest point in that direction + half the leftover space on the screen
        leftScreenLon = leftMax
                        - 0.5 * ((canvas.getWidth() / pxDegRatio) - (rightMax - leftMax));
        rightScreenLon = rightMax
                        + 0.5 * ((canvas.getWidth() / pxDegRatio) - (rightMax - leftMax));
        topScreenLat = topMax
                        + 0.5 * ((canvas.getHeight() / pxDegRatio) - (topMax - bottomMax));
        bottomScreenLat = bottomMax
                        - 0.5 * ((canvas.getHeight() / pxDegRatio) - (topMax - bottomMax));
    }

    private void scaleMap(Canvas canvas) {

        //src rect default parameters
        int srcTop = 0;
        int srcBottom = trailWalk.map.getHeight();
        int srcLeft = 0;
        int srcRight = trailWalk.map.getWidth();

        //dest rect default parameters
        int destTop = 0;
        int destBottom = canvas.getHeight();
        int destLeft = 0;
        int destRight = canvas.getWidth();

        //if the top lat of the map is above the top lat of the screen
        if (trailWalk.topLeftMap.getLatitude() > topScreenLat) {
            //lower the src Rect's top edge
            srcTop =        (int)   (((trailWalk.topLeftMap.getLatitude()
                                            - topScreenLat)
                                        / (trailWalk.topLeftMap.getLatitude()
                                            - trailWalk.bottomRightMap.getLatitude()))
                                    * trailWalk.map.getHeight());
        }
        //else the top lat of the map is below the top lat of the screen
        else {
            //lower the dest Rect's top edge
            destTop =       (int)   (((topScreenLat
                                            - trailWalk.topLeftMap.getLatitude())
                                        / (topScreenLat
                                            - bottomScreenLat))
                                    * canvas.getHeight());
        }
        //if the bottom lat of the map is below the bottom lat of the screen
        if (trailWalk.bottomRightMap.getLatitude() < bottomScreenLat) {
            //raise the src Rect's bottom edge
            srcBottom =     (int)   ((1 - ((bottomScreenLat
                                            - trailWalk.bottomRightMap.getLatitude())
                                        / (trailWalk.topLeftMap.getLatitude()
                                            - trailWalk.bottomRightMap.getLatitude())))
                                    * trailWalk.map.getHeight());
        }
        //else the bottom lat of the map is above the bottom lat of the screen
        else {
            //raise the dest Rect's bottom edge
            destBottom =    (int)   ((1 - ((trailWalk.bottomRightMap.getLatitude()
                                            - bottomScreenLat)
                                        / (topScreenLat
                                            - bottomScreenLat)))
                                    * canvas.getHeight());
        }
        //if the left lon of the map is left of the left lon of the screen
        if (trailWalk.topLeftMap.getLongitude() < leftScreenLon) {
            //move right the src Rect's left edge
            srcLeft =       (int)   (((leftScreenLon
                                            - trailWalk.topLeftMap.getLongitude())
                                        / (trailWalk.bottomRightMap.getLongitude()
                                            - trailWalk.topLeftMap.getLongitude()))
                                    * trailWalk.map.getWidth());
        }
        //else the left lon of the map is to the right of the left lon of the screen
        else {
            //move right the dest Rect's left edge
            destLeft =      (int)   (((trailWalk.topLeftMap.getLongitude()
                                            - leftScreenLon)
                                        / (rightScreenLon
                                            - leftScreenLon))
                                    * canvas.getWidth());
        }
        //if the right lon of the map is to the right of the right lon of the screen
        if (trailWalk.bottomRightMap.getLongitude() > rightScreenLon) {
            //move left the src Rect's right edge
            srcRight =      (int)   ((1 - ((trailWalk.bottomRightMap.getLongitude()
                                            - rightScreenLon)
                                        / (trailWalk.bottomRightMap.getLongitude()
                                            - trailWalk.topLeftMap.getLongitude())))
                                    * trailWalk.map.getWidth());
        }
        //else the right lon of the map is to the left of the right lon of the screen
        else {
            //move left the dest Rect's right edge
            destRight =     (int)   ((1 - ((rightScreenLon
                                            - trailWalk.bottomRightMap.getLongitude())
                                        / (rightScreenLon
                                            - leftScreenLon)))
                                    * canvas.getWidth());
        }

        //create the Rects based on the above calculated values
        mapSrcRect = new Rect(srcLeft, srcTop, srcRight, srcBottom);
        mapDestRect = new Rect(destLeft, destTop, destRight, destBottom);
    }

    //credit to Bachi on StackOverflow for the code below
    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
