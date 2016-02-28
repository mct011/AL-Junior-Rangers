package com.au_team11.aljuniorrangers;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.popup.PopupContainer;
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
    public static final float NEARBY_RADIUS_DP = 10;

    //Parent Activity
    Activity activity;

    //used in isNearOnScreen for calculating proximity
    int pxPerDp;

    View view;

    //popup for normal ActionPoint
    PopupMenu popup;

    //textview where "popup" will dump its info
    TextView actionPointPopup;
    //the above textview container
    ScrollView actionPointPopupContainer;
    //whether the "popup" is active, to allow for popup removal
    Boolean popupActive = false;

    //used to test REST data requests
    //String featureServiceURL0 = "https://conservationgis.alabama.gov/adcnrweb/rest/services/Trails_SLD/MapServer/2";
    //String featureServiceURL1 = "https://conservationgis.alabama.gov/adcnrweb/rest/services/Trails_SLD/MapServer/1";
    //ArcGISFeatureLayer featureLayer0;
    //ArcGISFeatureLayer featureLayer1;

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
        this.activity =  activity;
        pxPerDp =   activity.getApplicationContext().getResources().getDisplayMetrics().densityDpi
                  / activity.getApplicationContext().getResources().getDisplayMetrics().DENSITY_DEFAULT;
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

        //get textview from layout
        actionPointPopup = (TextView) view.findViewById(R.id.ActionButtonPopup);
        //get the textview container from layout
        actionPointPopupContainer = (ScrollView) view.findViewById(R.id.ActionButtonPopupContainer);
        //set it to GONE to prevent clicks
        actionPointPopupContainer.setVisibility(View.GONE);

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
        //featureLayer0 = new ArcGISFeatureLayer(featureServiceURL0, ArcGISFeatureLayer.MODE.ONDEMAND);
        //featureLayer1 = new ArcGISFeatureLayer(featureServiceURL1, ArcGISFeatureLayer.MODE.ONDEMAND);
        //mapView.addLayer(featureLayer0);
        //mapView.addLayer(featureLayer1);

        //initialize the GraphicsLayer
        graphicsLayer = new GraphicsLayer();

        //create the action points
        //fileName = getArguments().getString(getResources().getString(R.string.AssetBundleKey));
        //TODO: replace hard coded string below with asset bundle string above in final versionn
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
                //this also determines if the map is ready for user interaction
                if (locationDisplayManager != null) {

                    //if the popup is active
                    if (popupActive) {
                        //make the "popup" disappear
                        actionPointPopupContainer.setVisibility(View.GONE);
                        //record that the popup has disappeared
                        popupActive = false;
                    }
                    //else if the user tapped their current location
                    else if (isNearOnScreen(
                            mapView.toScreenPoint(
                                    locationDisplayManager.getPoint()),
                            new Point(x, y),
                            NEARBY_RADIUS_DP)) {

                        //DO THING AT CURRENT LOCATION
                        Log.i("ArcGIS", "click is near current location");

                    }
                    //else see if the user tapped any of the JSON defined points
                    else {

                        //for every defined point
                        for (int i = 0; i < actionPoints.size(); i++) {

                            //so we don't have to keep calc'ing references
                            final ActionPoint currentActionPoint = actionPoints.get(i);

                            //if the click is near to the current actionPoint
                            if (isNearOnScreen(
                                    mapView.toScreenPoint(
                                            currentActionPoint.getLocation()),
                                    new Point(x, y),
                                    NEARBY_RADIUS_DP)) {

                                /*
                                //show popup with clickable text
                                popup = new PopupMenu(
                                        activity.getApplicationContext(),
                                        view.findViewById(R.id.popupAnchor));
                                popup.inflate(R.menu.menu_popup);
                                //add text defined in JSON file to menu and define text click to do corresponding action
                                popup.getMenu().add(
                                        currentActionPoint.getText()).setOnMenuItemClickListener(
                                        new MenuItem.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem item) {
                                                //do the action associated with the popup
                                                currentActionPoint.action();
                                                return true;
                                            }
                                        });

                                //make the popup visible
                                popup.show();
                                */

                                //set text in "popup" to the ActionPoint's text
                                actionPointPopup.setText(currentActionPoint.getText());
                                //make the text clickable
                                actionPointPopup.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //when clicked, do the described action
                                        currentActionPoint.action();
                                    }
                                });
                                //make the container visible
                                actionPointPopupContainer.setVisibility(View.VISIBLE);
                                //record that the container is visible
                                popupActive = true;

                                Log.i("ArcGIS", "click is near point with index" + i);

                                //break from loop after action
                                i = actionPoints.size();
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
                //TODO: define types of ActionPoints and add conditionals to this loop
                //create new ActionPoint using Point generated from JSON file's lat/lon pair
                JSONObject arrayObject= jsonArray.getJSONObject(i);
                //if the ActionPoint is a picture AP
                if (arrayObject.getString("type").equals("picture")) {
                    newActionPoints.add(
                            new ActionPointPicture(
                                    activity,
                                    new Point(
                                            jsonArray.getJSONObject(i).getDouble("longitude"),
                                            jsonArray.getJSONObject(i).getDouble("latitude")),
                                    jsonArray.getJSONObject(i).getString("text")));
                }
                else {
                    newActionPoints.add(
                            new ActionPoint(
                                    activity,
                                    new Point(
                                            jsonArray.getJSONObject(i).getDouble("longitude"),
                                            jsonArray.getJSONObject(i).getDouble("latitude")),
                                    jsonArray.getJSONObject(i).getString("text")));
                }
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
