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

/**
 * Created by Zac on 1/31/2016.
 */
public class WordSearchAdapter extends BaseAdapter {
    // words and wordsLeft are now in WordBankAdapter
//    private String[] words;
//    private int wordsLeft;
    private ArrayList<TextView> currGuess = new ArrayList<TextView>();
    private Context ctx;
    // dir now assigned value in constructor
    private Direction dir;
    // wordBank added to connect the two
    private WordBankAdapter wordBank;
    // added to know the x and y dist between two letters
    private int horzSpac;
    private int vertSpac;
    // row number: index / 6
    // column number: index % 6
    private String[] grid = {
            "T", "M", "C", "D", "E", "F",
            "R", "B", "A", "D", "E", "F",
            "E", "B", "C", "P", "E", "F",
            "E", "B", "C", "D", "L", "F",
            "A", "C", "C", "D", "E", "E",
            "A", "H", "C", "D", "E", "F",
            "A", "E", "C", "D", "E", "F",
            "A", "W", "C", "D", "E", "F",
            "A", "A", "C", "D", "E", "F",
            "A", "C", "C", "D", "E", "F",
            "A", "L", "L", "E", "A", "F",
            "A", "A", "C", "D", "E", "F"
    };

    public WordSearchAdapter(Context contextIn) {
        ctx = contextIn;
//        words = ctx.getResources().getStringArray(R.array.word_bank_1);
//        wordsLeft = words.length;
        dir = null;
    }

    public int getCount() {
        return grid.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public void setWordBankAdapter(WordBankAdapter wba)
    {
        wordBank = wba;
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
        tv.setText(grid[position]);
        tv.setTextColor(Color.BLACK);
        tv.setOnTouchListener(new TextTouchListener());

        return tv;
    }

    /**
     * Checks to see if currGuess contains a word.
     */
    public void checkGuess()
    {
        // Check that guess is of a valid length
        if (currGuess.size() > 1)
        {
            String guess = "";
            // Pull word from TextViews
            for (TextView tv : currGuess)
                guess += tv.getText();
            // Compare guess to each word to see if there's a match

            // if curGuess contains a valid word
            if (wordBank.guess(guess))
            {
                // mark word as green
                for (TextView tv : currGuess) {
                    tv.setTextColor(Color.GREEN);
                    // Going to modify listener to add an if Color.GREEN section
//                    tv.setOnTouchListener(null);
                }

                // Reset currGuess and dir
                currGuess = new ArrayList<TextView>();
                dir = null;
            }
        }
    }

    /**
     * Touch listener which allows a letter to be pressed.
     * When pressed, the letter will toggle between black and blue.
     */
    private class TextTouchListener implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            // If a letter is touched
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                // Record it
                TextView tv = (TextView) v;

                /*
                Touch is handled based on the letter's color.
                If it's black/green: if it follows the current direction, the color is changed
                to blue/red.
                If it's blue/red: if it was the last letter guessed, the color is changed to
                black/green.
                 */
                switch(tv.getCurrentTextColor())
                {
                    // if the letter hasn't already been selected
                    case Color.GREEN:
                    case Color.BLACK:
                        // if second letter
                        if (currGuess.size() == 1)
                        {
                            TextView temp = currGuess.get(currGuess.size() - 1);

                            int xSpacing = Math.abs(temp.getLeft() - tv.getLeft());
                            int ySpacing = Math.abs(temp.getTop() - tv.getTop());

                            // set direction
                            if (xSpacing == 0)
                                dir = Direction.vertical;
                            else if (ySpacing == 0)
                                dir = Direction.horizontal;
                            else
                                dir = Direction.diagonal;

                            if (tv.getCurrentTextColor() == Color.BLACK)
                                tv.setTextColor(Color.BLUE);
                            else
                                tv.setTextColor(Color.RED);
                            currGuess.add(tv);
                        }
                        // if third or later letter
                        else if (currGuess.size() >= 2)
                        {
                            TextView temp = currGuess.get(currGuess.size() - 1);

                            int xSpacing = Math.abs(temp.getLeft() - tv.getLeft());
                            int ySpacing = Math.abs(temp.getTop() - tv.getTop());

                            // check number is in same direction
                            // TODO: This doesn't work if in same direction if >1 letter away. Fix it.
                            if ((xSpacing == 0 && dir == Direction.vertical)
                                    || (ySpacing == 0 && dir == Direction.horizontal)
                                    || (xSpacing > 0 && ySpacing > 0 && dir == Direction.diagonal))
                            {
                                if (tv.getCurrentTextColor() == Color.BLACK)
                                    tv.setTextColor(Color.BLUE);
                                else
                                    tv.setTextColor(Color.RED);
                                currGuess.add(tv);
                                checkGuess();
                            }
                        }
                        // if first letter
                        else
                        {
                            if (tv.getCurrentTextColor() == Color.BLACK)
                                tv.setTextColor(Color.BLUE);
                            else
                                tv.setTextColor(Color.RED);
                            currGuess.add(tv);
                            checkGuess();
                        }
                        break;
                    // if the letter has already been selected
                    case Color.BLUE:
                    case Color.RED:
                        TextView temp = currGuess.get(currGuess.size() - 1);
                        if (tv.getLeft() == temp.getLeft() &&
                                tv.getTop() == temp.getTop())
                        {
                            if (tv.getCurrentTextColor() == Color.BLUE)
                                tv.setTextColor(Color.BLACK);
                            else
                                tv.setTextColor(Color.GREEN);
                            currGuess.remove(currGuess.size() - 1);
                        }

                        // changed from "== 0" to "== 1"
                        if (currGuess.size() == 1)
                            dir = null;
                        break;
                }
            }
            return true;
        }
    }

    private enum Direction
    {
        vertical, horizontal, diagonal;
    }
}
