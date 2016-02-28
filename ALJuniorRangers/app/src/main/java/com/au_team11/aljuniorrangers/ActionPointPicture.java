package com.au_team11.aljuniorrangers;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

import com.esri.core.geometry.Point;

/**
 * Created by JDSS on 2/26/16.
 */
public class ActionPointPicture extends ActionPoint {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public ActionPointPicture(Activity newActivity, Point newLocation, String newText) {
        super(newActivity, newLocation, newText);
    }

    public void action() {
        super.action();
        //send camera intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getApplicationContext().getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
