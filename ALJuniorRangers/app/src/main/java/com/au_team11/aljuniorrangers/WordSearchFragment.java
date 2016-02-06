package com.au_team11.aljuniorrangers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

public class WordSearchFragment extends Fragment {

    private FragmentActivity fActivity;
    private LinearLayout lLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        fActivity = (FragmentActivity) super.getActivity();
        lLayout = (LinearLayout) inflater.inflate(R.layout.content_grid_view, container, false);

        android.widget.GridView gridView = (android.widget.GridView) fActivity.findViewById(R.id.gridview);
        gridView.setAdapter(new TextAdapter(super.getActivity()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
//                Toast.makeText(WordSearchFragment.this, "" + position,
//                        Toast.LENGTH_SHORT).show();
            }
        });

        lLayout.findViewById(gridView.getId());
        return lLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
    }
}
