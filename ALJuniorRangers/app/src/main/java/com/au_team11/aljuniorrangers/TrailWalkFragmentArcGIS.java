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
import com.esri.android.map.event.OnStatusChangedListener;

/**
 * Created by JDSS on 2/17/16.
 */
public class TrailWalkFragmentArcGIS extends Fragment {

    Context context;
    View view;

    MapView mapView;

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

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (o == mapView && status == STATUS.INITIALIZED) {
                    mapView.getLocationDisplayManager().start();
                }
            }
        });

        return view;
    }
}
