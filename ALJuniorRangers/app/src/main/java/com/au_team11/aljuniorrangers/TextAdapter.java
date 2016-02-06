package com.au_team11.aljuniorrangers;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Zac on 1/31/2016.
 */
public class TextAdapter extends BaseAdapter {
    private String[] words = {"TREE", "MAPLE", "LEAF", "CHEWACLA"};
    private int wordsLeft = words.length;
    private ArrayList<TextView> currentGuess = new ArrayList<TextView>();
    private Context ctx;
    private Direction dir = null;
    // 240 horizontal spacing
    // 190 vertical spacing
    private String[] letters = {
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

    // row number: index / 6
    // column number: index % 6
    private TextView[] grid = new TextView[letters.length];

    public TextAdapter(Context contextIn) {
        ctx = contextIn;
    }

    public int getCount() {
        return letters.length;
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

        tv.setText(letters[position]);
        tv.setTextColor(Color.BLACK);
        tv.setOnTouchListener(new TextTouchListener());

        return tv;
    }

    /**
     * Checks to see if current highlighted letters make a
     * valid word.
     */
    public void checkGuess()
    {
        // Check that guess is of a valid length
        if (currentGuess.size() > 1)
        {
            String guess = "";
            // Pull word from TextViews
            for (TextView tv : currentGuess)
                guess += tv.getText();
            // Compare guess to each word to see if there's a match
            for (int i = 0; i < words.length; i++) {
                String s = words[i];
                // If there is a match
                if (s.equals(guess)) {
                    // TODO: Print out that user found word

                    // Set letters so that they are locked from being touched
                    for (TextView tv : currentGuess) {
                        tv.setTextColor(Color.GREEN);
                        tv.setOnTouchListener(null);
                    }

                    // Empty guess list and set direction to null
                    currentGuess = new ArrayList<TextView>();
                    dir = null;

                    // Remove word from word bank
                    words[i] = words[words.length - 1];
                    words = Arrays.copyOf(words, words.length - 1);
                    wordsLeft = words.length;

                    // See if the user finished
                    // TODO: checkWin();
                }
            }
        }
    }

    /* TODO
    public void checkWin()
    {
        if(wordsLeft == 0)
            System.out.println("Congratulations, you won the word search!");
        else
            System.out.println("Doing great! You've got " + wordsLeft + " words left.");
    }
    */

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
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Record it
                TextView tv = (TextView) v;

                /*
                    If it is black, turn it blue and add the letter
                    to the guess list and see if the new guess list
                    is a valid word
                 */
                if (tv.getCurrentTextColor() == Color.BLACK)
                {
                    // if second letter
                    if (currentGuess.size() == 1)
                    {
                        TextView temp = currentGuess.get(currentGuess.size() - 1);

                        int xSpacing = Math.abs(temp.getLeft() - tv.getLeft());
                        int ySpacing = Math.abs(temp.getTop() - tv.getTop());

                        // set direction
                        if (xSpacing == 0)
                            dir = Direction.vertical;
                        else if (ySpacing == 0)
                            dir = Direction.horizontal;
                        else
                            dir = Direction.diagonal;

                        tv.setTextColor(Color.BLUE);
                        currentGuess.add(tv);
                    }
                    // if third or later letter
                    else if (currentGuess.size() >= 2)
                    {
                        TextView temp = currentGuess.get(currentGuess.size() - 1);

                        int xSpacing = Math.abs(temp.getLeft() - tv.getLeft());
                        int ySpacing = Math.abs(temp.getTop() - tv.getTop());

                        // check number is in same direction
                        if ((xSpacing == 0 && dir == Direction.vertical) || (ySpacing == 0 && dir == Direction.horizontal)
                                || (xSpacing > 0 && ySpacing > 0 && dir == Direction.diagonal))
                        {
                            tv.setTextColor(Color.BLUE);
                            currentGuess.add(tv);
                            checkGuess();
                        }
                    }
                    // if first letter
                    else
                    {
                        tv.setTextColor(Color.BLUE);
                        currentGuess.add(tv);
                        checkGuess();
                    }
                }

                /*
                    If it is blue, make sure it's the last letter selected
                    (this way no disconnected letter groupings happen), if
                    it is, then turn it black and remove it from the guess
                    list.
                 */
                else if (tv.getCurrentTextColor() == Color.BLUE)
                {
                    TextView temp = currentGuess.get(currentGuess.size() - 1);
                    if (tv.getLeft() == temp.getLeft() &&
                            tv.getTop() == temp.getTop())
                    {
                        // System.out.println(tv.getText() + " deselected");
                        tv.setTextColor(Color.BLACK);
                        currentGuess.remove(currentGuess.size() - 1);
                    }

                    if (currentGuess.size() == 0)
                        dir = null;
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
