package com.au_team11.aljuniorrangers;

/**
 * Created by JDSS on 1/30/16.
 */
public class ParkActivity {

    Boolean completed;

    public ParkActivity() {
        completed = false;
    }

    public ParkActivity(Boolean alreadyCompleted) {
        completed = alreadyCompleted;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
