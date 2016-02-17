package com.au_team11.aljuniorrangers;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by JDSS on 2/13/16.
 */
public class MainMenuFragment extends Fragment {

    public static final String MAINMENU_DATA = "mainmenu_data.json";
    public static final String PARKARRAY_KEY = "array";
    public static final String PARKNAME_KEY = "name";
    public static final String PARKFILE_KEY = "file";


    //MainActivity listener for park selection
    ParkListener mCallback;

    //View reference, for recreating from previous state
    View view;

    //Screen widgets
    ImageView backgroundImage;
    Spinner parkSpinner;
    Button parkSelector;

    //Spinner data
    String parkDataJSON;
    JSONArray parkJSONArray;
    ArrayList<String> spinnerStrings;
    ArrayAdapter<String> spinnerStringAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //if view already exists, return it instead of recreating
        if (view != null) {
            //back button causes exception if this line left in
            //says view.getParent() returns null, can't do removeView(view) on null object
            //((ViewGroup) view.getParent()).removeView(view);
            return view;
        }

        //get a reference to the main activitiy
        mCallback = (ParkListener) this.getActivity();

        //inflate the view
        view = inflater.inflate(R.layout.mainmenu_layout, container, false);

        //connect objects to screen widgets
        backgroundImage = (ImageView) view.findViewById(R.id.backgroundImage);
        parkSpinner = (Spinner) view.findViewById(R.id.parkSpinner);
        parkSelector = (Button) view.findViewById(R.id.parkSelector);

        //Read data from main menu data file
        parkDataJSON = loadJSONFromAsset(MAINMENU_DATA);
        //initialize spinnerStrings
        spinnerStrings = new ArrayList<String>();
        try {
            //load the json data into parkJSONArray
            parkJSONArray = new JSONObject(parkDataJSON).getJSONArray(PARKARRAY_KEY);

            //iterate through elements in parkJSONArray, adding park names to spinnerStrings
            for (int i = 0; i < parkJSONArray.length(); i++)
                spinnerStrings.add(parkJSONArray.getJSONObject(i).getString(PARKNAME_KEY));

            //create spinnerStringAdapter and fill with spinnerStrings
            spinnerStringAdapter = new ArrayAdapter<String>(this.getActivity().getApplicationContext(),
                                                            R.layout.mainmenu_spinner_textview,
                                                            spinnerStrings);

            //populate parkSpinner with elements in spinnerStringAdapter
            parkSpinner.setAdapter(spinnerStringAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //attach listener to the parkSelector button
        parkSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get selected item from parkSpinner
                String selectedItem = (String) parkSpinner.getSelectedItem();
                //find selected item in the parkJSONArray
                try {
                    //look through all the elements in parkJSONArray
                    for (int i = 0; i < parkJSONArray.length(); i++) {
                        //if this object's name matches the selected name
                        if (parkJSONArray.getJSONObject(i).getString(PARKNAME_KEY).equals(selectedItem)) {
                            //callback to the main activity with the park data file to load
                            mCallback.onParkSelectedListener(parkJSONArray.getJSONObject(i).getString(PARKFILE_KEY));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

}
