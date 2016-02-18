package com.au_team11.aljuniorrangers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esri.android.map.MapView;

/**
 * Created by JDSS on 2/17/16.
 */
public class TrailWalkFragmentArcGIS extends Fragment {

    MapView mapView;

    Context context;
    View view;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context =  activity.getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            ((ViewGroup) view.getParent()).removeView(view);
            return view;
        }

        view = inflater.inflate(R.layout.trailwalk_layout_arcgis, container, false);

        return view;
    }
}
