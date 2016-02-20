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

    public static final int MARKER_SIZE_DP = 8;
    public static final float NEARBY_RADIUS_DP = 32;

    Context context;
    int densityDpi;
    int density_default;

    View view;

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
        densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        density_default = context.getResources().getDisplayMetrics().DENSITY_DEFAULT;
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
        //get pixel value of tolerance
        float tolerancePX = toleranceDP * (densityDpi / density_default);
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
