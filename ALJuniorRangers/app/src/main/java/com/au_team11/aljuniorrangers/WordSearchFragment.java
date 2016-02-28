package com.au_team11.aljuniorrangers;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

public class WordSearchFragment extends Fragment {

    private GridView wordSearch;
    private GridView wordBank;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.content_grid_view, container, false);

        wordSearch = (GridView) view.findViewById(R.id.word_search);
        wordBank = (GridView) view.findViewById(R.id.word_bank);

        // added to allow the two to communicate between each other
        WordSearchAdapter wsa = new WordSearchAdapter(super.getActivity());
        WordBankAdapter wba = new WordBankAdapter(super.getActivity());
        wsa.setWordBankAdapter(wba);
        wba.setWordSearchAdapter(wsa);

        wordSearch.setAdapter(wsa);
        wordBank.setAdapter(wba);
        wordBank.setColumnWidth(wordBank.getWidth() / 2);


        wordSearch.setOnItemClickListener(null);
        wordBank.setOnItemClickListener(null);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
    }
}
