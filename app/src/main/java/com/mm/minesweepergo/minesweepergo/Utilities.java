package com.mm.minesweepergo.minesweepergo;

import android.app.Application;
import android.content.Context;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.mm.minesweepergo.minesweepergo.DomainModel.User;

import java.io.ByteArrayOutputStream;

/**
 * Created by Milos on 6/5/2017.
 */



public class Utilities
{
    public static byte[] getByteArrayFromBitmap(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 60, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static User readUserPreferences()
    {
        User retUser = new User();



        return  retUser;
    }
}
