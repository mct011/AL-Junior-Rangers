package com.au_team11.aljuniorrangers;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrailWalkFragment extends Fragment {

    TrailWalk trailWalk;

    View view;

    TrailWalkView trailWalkView;

    public TrailWalkFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("TrailWalkFragment", "super.onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i("TrailWalkFragment", "onCreateView");

        if (view != null) {
            ((ViewGroup) view.getParent()).removeView(view);
            return view;
        }

        view = inflater.inflate(R.layout.trailwalk_layout, container, false);

        //create trailWalk object using filename stored in arguments bundle
        trailWalk = createTrailWalk(getArguments().getString(getResources().getString(R.string.assetBundleKey)));

        //find the created trailWalkView
        trailWalkView = (TrailWalkView) view.findViewById(R.id.trailWalkView);
        //give it a reference to the trailWalk object created earlier
        trailWalkView.setTrailWalk(trailWalk);

        //get location service
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //add functionality to location changes
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //what to do when the current location changes
                updateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);

        return view;
    }

    public void updateLocation(Location location) {
        //update current location in the TrailWalk object
        trailWalk.setCurrentPosition(new Coordinate(location.getLatitude(), location.getLongitude()));
        //redraw the screen
        trailWalkView.invalidate();
    }

    public TrailWalk createTrailWalk(String assetFileName) {

        //build TrailWalk from asset file named with assetFileName
        ArrayList<Coordinate> newWaypoints = new ArrayList<Coordinate>();

        String json = loadJSONFromAsset(assetFileName);
        Bitmap map = null;
        Coordinate topLeftMap = null;
        Coordinate bottomRightMap = null;
        //credit goes to GrlsHu on StackOverflow
        try {
            //create new JSON Object
            JSONObject jsonObject = new JSONObject(json);
            //read the waypoint array from the object
            JSONArray jsonArray = jsonObject.getJSONArray("waypoints");

            //iterate through json array values, creating new Coordinate objects from the values
            for (int i = 0; i < jsonArray.length(); i++) {
                newWaypoints.add(
                        new Coordinate(
                                jsonArray.getJSONObject(i).getDouble("latitude"),
                                jsonArray.getJSONObject(i).getDouble("longitude")));
            }

            topLeftMap = new Coordinate(
                            jsonObject.getJSONObject("map").getJSONObject("topLeft").getDouble("latitude"),
                            jsonObject.getJSONObject("map").getJSONObject("topLeft").getDouble("longitude"));

            bottomRightMap = new Coordinate(
                            jsonObject.getJSONObject("map").getJSONObject("bottomRight").getDouble("latitude"),
                            jsonObject.getJSONObject("map").getJSONObject("bottomRight").getDouble("longitude"));

            //TODO: prevent from recreating the bitmap if the previous one is still in memory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            map = BitmapFactory.decodeStream(
                                    getResources().getAssets().open(jsonObject.getJSONObject("map").getString("name")),
                                    null,
                                    options);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException i) {
           i.printStackTrace();
        }

        TrailWalk newTrailWalk = new TrailWalk(false, newWaypoints, map);
        //assume the user is starting at the trail start, will update if not the case
        newTrailWalk.setCurrentPosition(
                new Coordinate(
                        newWaypoints.get(0).getLatitude(),
                        newWaypoints.get(0).getLongitude()));
        newTrailWalk.topLeftMap = topLeftMap;
        newTrailWalk.bottomRightMap = bottomRightMap;

        return newTrailWalk;
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
