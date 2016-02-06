package com.au_team11.aljuniorrangers;

import android.os.Bundle;
import android.app.Fragment;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

public class WordSearchFragment extends Fragment {

    //private Activity fActivity;
    //private LinearLayout lLayout;
    private GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.content_grid_view, container, false);

        gridView = (GridView) view.findViewById(R.id.gridview);

        /*
        fActivity = (Activity) super.getActivity();
        lLayout = (LinearLayout) inflater.inflate(R.layout.content_grid_view, container, false);

        android.widget.GridView gridView = (android.widget.GridView) fActivity.findViewById(R.id.gridview);
        */
        gridView.setAdapter(new TextAdapter(super.getActivity()));



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
//                Toast.makeText(WordSearchFragment.this, "" + position,
//                        Toast.LENGTH_SHORT).show();
            }
        });

        //lLayout.findViewById(gridView.getId());
        //return lLayout;
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
    }
}
