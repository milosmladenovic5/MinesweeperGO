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

public class Utilities extends Application
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

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
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

        SharedPreferences sharedPref = getContext().getSharedPreferences(
                "UserInfo", Context.MODE_PRIVATE);

        retUser.username = sharedPref.getString("Username", "empty");
        retUser.password = sharedPref.getString("Password", "empty");
        retUser.email = sharedPref.getString("Email","empty");
        retUser.lastName = sharedPref.getString("LastName", "empty");
        retUser.firstName = sharedPref.getString("FirstName", "empty");
        retUser.imagePath = sharedPref.getString("ImagePath", "empty");
        retUser.btDevice = sharedPref.getString("BtDevice", "empty");

        return  retUser;
    }
}
