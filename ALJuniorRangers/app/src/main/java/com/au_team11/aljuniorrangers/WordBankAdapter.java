package com.au_team11.aljuniorrangers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Zac on 2/19/2016.
 */
public class WordBankAdapter {
    private String[] words = {"TREE", "MAPLE", "LEAF", "CHEWACLA"};
    private int wordsLeft = words.length;
    private ArrayList<TextView> currentGuess = new ArrayList<TextView>();
    private Context ctx;

    // row number: index / 2
    // column number: index % 2
    private TextView[] grid = new TextView[words.length];

    public WordBankAdapter(Context contextIn) {
        ctx = contextIn;
    }

    public int getCount() {
        return words.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        if (convertView == null) {
            tv = new TextView(ctx);
            tv.setLayoutParams(new GridView.LayoutParams(85, 85));
        } else {
            tv = (TextView) convertView;
        }

        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setText(words[position]);
        tv.setTextColor(Color.RED);
        tv.setOnTouchListener(new WordTouchListener());

        return tv;
    }

    private class WordTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }

    }
}
