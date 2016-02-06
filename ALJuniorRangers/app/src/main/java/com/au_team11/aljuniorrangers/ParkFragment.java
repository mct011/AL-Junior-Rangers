package com.au_team11.aljuniorrangers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class ParkFragment extends Fragment {

    Spinner spinner;
    Button goButton;
    ArrayAdapter<String> dataAdapter;
    ArrayList<String> categories;
    String selectedActivity;
    JSONArray activitiesPopulation;

    //listener
    ParkActivityListener mCallback;

    public ParkFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize listener
        mCallback = (ParkActivityListener) this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i("ParkFragment", "super.onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i("ParkFragment", "onCreateView");
        View view = inflater.inflate(R.layout.park_layout, container, false);

        String parkActivities = getArguments().getString("activities");
        //TODO Do we really need this? Discuss what exactly will be passed between fragments, through bundle or intent. If bundles why not just use a JSONobject.toString and then delimit the string for the activities.


        //Instantiate Elements
        selectedActivity = "";
        spinner = (Spinner) view.findViewById(R.id.spinner);
        goButton=(Button) view.findViewById(R.id.GoButton);

        // Spinner Drop Down Elements
        categories = new ArrayList<String>();

        //Place Holder Elements till JSON is figured out
        //TODO JSON Stuff/possibly delimit string for different activities.
        activitiesPopulation = populateSpinner(getArguments().getString("activities"));
        //extract values from JSONArray
        try {
            for (int i = 0; i < activitiesPopulation.length(); i++) {
                categories.add(activitiesPopulation.getJSONObject(i).getString("activityName"));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, categories);

        //Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        //setting on click listener to spinner, mostly for testing
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedActivity = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addListnerOnButton();
        return view;
    }

    private void addListnerOnButton() {
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String activityValue = spinner.getSelectedItem().toString();
                //Send filename back to activitiy
                returnFilename(activityValue);
            }
        });
    }

    private JSONArray populateSpinner(String filename){
        String jsonString = loadJSONFromAsset(filename);
        JSONArray sendArray = null;
        try {
            //create new JSON Object
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray activitiesArray = jsonObject.getJSONArray("activities");
            sendArray = activitiesArray;

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return sendArray;
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

    public void returnFilename(String activityName){
        String filename = "";
        //also need activity type
        String type = "";
        //parse JSONArray for activityname specified by spinner. Then get correct filename.
        try {
            for (int i = 0; i < activitiesPopulation.length(); i++) {
                if (activityName.equals(activitiesPopulation.getJSONObject(i).getString("activityName"))) {
                    filename = activitiesPopulation.getJSONObject(i).getString("filename");
                    type = activitiesPopulation.getJSONObject(i).getString("type");
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        if (filename == "") {
            //DO Some Error Reporting
        }

        /*
        //Package the filename in an intent and send to Main
        Intent intent = new Intent(getActivity().getBaseContext(), MainActivity.class);
        intent.putExtra("toOpen", filename);
        getActivity().startActivity(intent);
        */

        //send the filename through a callback method
        mCallback.onParkActivitySelectedListener(filename, type);
    }
}