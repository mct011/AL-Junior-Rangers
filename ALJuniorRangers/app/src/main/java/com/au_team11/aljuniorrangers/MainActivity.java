package com.au_team11.aljuniorrangers;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

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
            args.putString("ParkActivitiesString", "test_activities.json");
            fragmentManager.beginTransaction().add(R.id.activity_main, parkFragment).commit();
        }

    }

    @Override
    public void onBackPressed() {
        //will exit the app or pop backstack willy nilly
        //super.onBackPressed();

        //will put the trailwalk on screen
        trailWalkFragment = new TrailWalkFragment();
        Bundle arguments = new Bundle();
        arguments.putString(getResources().getString(R.string.assetBundleKey), "trail_test_2.json");
        trailWalkFragment.setArguments(arguments);
        //put the fragment on the screen
        fragmentManager.beginTransaction().replace(R.id.activity_main, trailWalkFragment).commit();

    }

    public void onParkSelectedListener(String parkFileName) {

    }

    public void onTrailWalkSelectedListener(String trailWalkFileName) {

    }

    public void onWordSearchSelectedListener(String wordSearchFileName) {

    }

}
