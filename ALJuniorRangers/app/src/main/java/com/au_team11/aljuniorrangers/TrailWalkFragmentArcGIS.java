package com.au_team11.aljuniorrangers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.popup.PopupContainer;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.symbol.SimpleMarkerSymbol;

/**
 * Created by JDSS on 2/17/16.
 */
public class TrailWalkFragmentArcGIS extends Fragment {

    //how close a click must be to a point to trigger an action
    public static final float NEARBY_RADIUS_DP = 32;

    Context context;
    //used in isNearOnScreen for calculating proximity
    int pxPerDp;

    View view;

    //used to test REST data requests
    String featureServiceURL = "https://conservationgis.alabama.gov/adcnrweb/rest/services/StateParksSingle/MapServer/0";
    ArcGISFeatureLayer featureLayer;


    //the map on screen
    MapView mapView;
    PopupContainer popupContainer;
    //controls displaying the current device location
    LocationDisplayManager locationDisplayManager;
    //how the map is drawn
    SpatialReference spatialReference;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context =  activity.getApplicationContext();
        pxPerDp =   context.getResources().getDisplayMetrics().densityDpi
                  / context.getResources().getDisplayMetrics().DENSITY_DEFAULT;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //check if view already exists
        if (view != null) {
            ((ViewGroup) view.getParent()).removeView(view);
            return view;
        }

        //inflate view from layout
        view = inflater.inflate(R.layout.trailwalk_layout_arcgis, container, false);

        //get reference to the map
        mapView = (MapView) view.findViewById(R.id.map);
        //mapview initialization must occur only after the map is ready
        mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (o == mapView && status == STATUS.INITIALIZED) {
                    //get the spatial reference of the map to sync with layers
                    spatialReference = mapView.getSpatialReference();
                    //get location display manager
                    locationDisplayManager = mapView.getLocationDisplayManager();
                    //tell the map to show the current location
                    locationDisplayManager.setShowLocation(true);
                    //start location tracking
                    locationDisplayManager.start();
                }
            }
        });

        //add REST requested feature layer
        featureLayer = new ArcGISFeatureLayer(featureServiceURL, ArcGISFeatureLayer.MODE.ONDEMAND);
        mapView.addLayer(featureLayer);

        //what to do when the user taps the screen at point x,y
        mapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float x, float y) {
                //location display manager can't be null for this
                if (locationDisplayManager != null) {
                    //if the current location on screen is near to the tap location on screen
                    if (isNearOnScreen(mapView.toScreenPoint(locationDisplayManager.getPoint()), new Point(x, y), NEARBY_RADIUS_DP)) {
                        Log.i("ArcGIS", "click is near current location");
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (locationDisplayManager != null && !locationDisplayManager.isStarted())
            locationDisplayManager.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locationDisplayManager != null)
            locationDisplayManager.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //pause location display manager to save battery
        locationDisplayManager.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationDisplayManager.stop();
    }

    //returns true if the two points are within a square with side length toleranceDP of each other
    public Boolean isNearOnScreen(Point point0, Point point1, float toleranceDP) {
        //check that points are not null
        if (point0 == null || point1 == null) {
            return false;
        }
        //get pixel value of tolerance
        float tolerancePX = toleranceDP * pxPerDp;
        //if the one of the points is outside the tolerance range, return false
        if ((point0.getX() >= (point1.getX() - tolerancePX)) &&
            (point0.getX() <= (point1.getX() + tolerancePX)) &&
            (point0.getY() >= (point1.getY() - tolerancePX)) &&
            (point0.getY() <= (point1.getY() + tolerancePX)))
            return true;
        else
            return false;
    }
}
