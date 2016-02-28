package com.au_team11.aljuniorrangers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Zac on 2/19/2016.
 */
public class WordBankAdapter extends BaseAdapter{
    private String[] words;
    private Context ctx;
    // wordSearch added to connect the two
    private WordSearchAdapter wordSearch;
    // row number: index / 2
    // column number: index % 2
    private TextView[] bank;

    public WordBankAdapter(Context contextIn) {
        ctx = contextIn;
        words = ctx.getResources().getStringArray(R.array.word_bank_1);
        bank = new TextView[words.length];
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

    public void setWordSearchAdapter(WordSearchAdapter wsa)
    {
        wordSearch = wsa;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        if (convertView == null) {
            tv = new TextView(ctx);
            tv.setLayoutParams(new GridView.LayoutParams(500, 100));
        } else {
            tv = (TextView) convertView;
        }

        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setText(words[position]);
        tv.setTextColor(Color.BLACK);
        tv.setOnTouchListener(new WordTouchListener());
        bank[position] = tv;

        return tv;
    }

    /*
    The highlighted word on the word search will be checked to see if it
    matches a word in the bank. If it does, then it's crossed out.
     */
    public boolean guess(String word)
    {
        for(int i = 0; i < words.length; i++)
        {
            if (words[i].equals(word))
            {
                words[i] = words[words.length-1];
                words = Arrays.copyOf(words, words.length-1);
                // cross word off bank
                crossOut(word);
                checkWin();
                return true;
            }
        }
        return false;
    }

    /*
    Strikes thru a word.
     */
    public void crossOut(String word)
    {
        for (TextView tv : bank)
        {
            if (tv.getText().equals(word))
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    /* TODO
    Sees if there are any words left, if not, then display a splash screen
    congratulating the user, awarding them points and exiting after.
     */
    public void checkWin()
    {
        if (words.length == 0)
        {
            // do things here
        }
    }

    private class WordTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                TextView tv = (TextView) v;

                PopupMenu popup = new PopupMenu(ctx, tv);

                // TODO: if word is crossed out, say that word has been found else say that word is still there
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

//                Toast.makeText(ctx, "You Clicked: " + tv.getText(), Toast.LENGTH_SHORT).show();
//                popup.show();

                popup.setOnMenuItemClickListener(null);
            }
            return true;
        }
    }
}
