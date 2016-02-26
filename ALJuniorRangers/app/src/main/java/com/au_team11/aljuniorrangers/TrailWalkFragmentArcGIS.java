package com.au_team11.aljuniorrangers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.popup.PopupContainer;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by JDSS on 2/17/16.
 */
public class TrailWalkFragmentArcGIS extends Fragment {

    //how close a click must be to a point to trigger an action
    public static final float NEARBY_RADIUS_DP = 32;

    //Parent Activity
    Context context;

    //used in isNearOnScreen for calculating proximity
    int pxPerDp;

    View view;

    //used to test REST data requests
    String featureServiceURL0 = "https://conservationgis.alabama.gov/adcnrweb/rest/services/Trails_SLD/MapServer/2";
    String featureServiceURL1 = "https://conservationgis.alabama.gov/adcnrweb/rest/services/Trails_SLD/MapServer/1";
    ArcGISFeatureLayer featureLayer0;
    ArcGISFeatureLayer featureLayer1;

    //the map on screen
    MapView mapView;
    PopupContainer popupContainer;
    //controls displaying the current device location
    LocationDisplayManager locationDisplayManager;
    //how the map is drawn
    SpatialReference spatialReference;

    //trail data filename
    String fileName;
    //Points defined by the JSON file
    ArrayList<ActionPoint> actionPoints;
    GraphicsLayer graphicsLayer;

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

                    Log.i("TWFAGIS", "mapView spatial reference is: " + mapView.getSpatialReference().getText());

                }
            }
        });

        //add REST requested feature layer
        featureLayer0 = new ArcGISFeatureLayer(featureServiceURL0, ArcGISFeatureLayer.MODE.ONDEMAND);
        featureLayer1 = new ArcGISFeatureLayer(featureServiceURL1, ArcGISFeatureLayer.MODE.ONDEMAND);
        mapView.addLayer(featureLayer0);
        mapView.addLayer(featureLayer1);

        //initialize the GraphicsLayer
        graphicsLayer = new GraphicsLayer();

        //create the action points
        //fileName = getArguments().getString(getResources().getString(R.string.AssetBundleKey));
        //TODO: replace line below with line above in final version
        fileName = "test_trail_arcgis.json";
        String jsonData = loadJSONFromAsset(fileName);
        actionPoints = createActionPoints(jsonData);
        //add actionPoints to the graphics layer
        for(int i = 0; i < actionPoints.size(); i++) {
            graphicsLayer.addGraphic(
                    new Graphic(
                            actionPoints.get(i).getLocation(),
                            new SimpleMarkerSymbol(
                                    Color.RED,
                                    10,
                                    SimpleMarkerSymbol.STYLE.CIRCLE)));
        }

        //add the graphics layer to the map
        mapView.addLayer(graphicsLayer);

        //what to do when the user taps the screen at point x,y
        mapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float x, float y) {
                //location display manager can't be null for this
                if (locationDisplayManager != null) {
                    //if the user tapped their current location
                    if (isNearOnScreen(mapView.toScreenPoint(locationDisplayManager.getPoint()), new Point(x, y), NEARBY_RADIUS_DP)) {
                        Log.i("ArcGIS", "click is near current location");
                    }
                    //else if the user tapped any of the JSON defined points
                    else {
                        for (int i = 0; i < actionPoints.size(); i++) {
                            if (isNearOnScreen(
                                    mapView.toScreenPoint(
                                            actionPoints.get(i).getLocation()),
                                    new Point(x, y),
                                    NEARBY_RADIUS_DP)) {
                                //do the action
                                actionPoints.get(i).action();
                                Log.i("ArcGIS", "click is near point with index" + i);
                            }
                        }
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

    public ArrayList<ActionPoint> createActionPoints(String json) {
        ArrayList<ActionPoint> newActionPoints = new ArrayList<ActionPoint>();
        //credit goes to GrlsHu on StackOverflow
        try {
            //create new JSON Object
            JSONObject jsonObject = new JSONObject(json);
            //read the action point array from the object
            JSONArray jsonArray = jsonObject.getJSONArray("actionpoints");

            //fill newActionPoints with ActionPoints from the json data
            for (int i = 0; i < jsonArray.length(); i++) {
                //create new ActionPoint using Point generated from JSON file's lat/lon pair
                newActionPoints.add(
                        new ActionPoint(
                                new Point(
                                        jsonArray.getJSONObject(i).getDouble("longitude"),
                                        jsonArray.getJSONObject(i).getDouble("latitude"))));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return newActionPoints;
    }

    //credit goes to GrlsHu on StackOverflow
    //returns a json string from the asset file
    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
