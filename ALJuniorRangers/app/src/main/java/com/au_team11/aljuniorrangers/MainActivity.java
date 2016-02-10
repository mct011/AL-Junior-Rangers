package com.au_team11.aljuniorrangers;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

//testing the ability of branching and merging - Nick Openshaw

public class MainActivity extends Activity implements ParkActivityListener {

    TrailWalkFragment trailWalkFragment = null;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainActivity", "onCreate");

        setContentView(R.layout.activity_main);

        fragmentManager = getFragmentManager();

        //create a new TrailWalkFragment
        //TODO: pass filename for object data in constructor arguments
        if (savedInstanceState == null) {

            /*
            trailWalkFragment = new TrailWalkFragment();
            Bundle arguments = new Bundle();
            arguments.putString(getResources().getString(R.string.assetBundleKey), "trail_test_2.json");
            trailWalkFragment.setArguments(arguments);
            //put the fragment on the screen
            fragmentManager.beginTransaction().add(R.id.activity_main, trailWalkFragment).commit();
            */

            /*
            WordSearchFragment wordSearchFragment = new WordSearchFragment();
            fragmentManager.beginTransaction().add(R.id.activity_main, wordSearchFragment).commit();
            */

            ParkFragment parkFragment = new ParkFragment();
            Bundle args = new Bundle();
            args.putString("activities", "test_park.json");
            parkFragment.setArguments(args);
            fragmentManager.beginTransaction().add(R.id.activity_main, parkFragment).commit();
        }

    }

    @Override
    public void onBackPressed() {
        //will exit the app or pop backstack willy nilly
        super.onBackPressed();

        //will put the park menu on screen

    }

    public void onParkSelectedListener(String parkFileName) {

    }

    public void onParkActivitySelectedListener(String fileName, String type) {

        if (type.equals("trailwalk")) {
            trailWalkFragment = new TrailWalkFragment();
            Bundle arguments = new Bundle();
            arguments.putString(getResources().getString(R.string.assetBundleKey), fileName);
            trailWalkFragment.setArguments(arguments);
            //put the fragment on the screen
            fragmentManager.beginTransaction().replace(R.id.activity_main, trailWalkFragment).addToBackStack(null).commit();
        }
        else if (type.equals("wordsearch")) {
            WordSearchFragment wordSearchFragment = new WordSearchFragment();
            fragmentManager.beginTransaction().replace(R.id.activity_main, wordSearchFragment).addToBackStack(null).commit();
        }
        else {
            //do nothing
        }
    }
}
