package com.mm.minesweepergo.minesweepergo.DomainModel;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by Milan Nikolić on 30-May-17.
 */

public class User implements Parcelable{
    public String username;
    public String password;
    public String email;
    public String phoneNumber;
    public String firstName;
    public String lastName;
    public Bitmap image;
    public String imagePath;
    public String btDevice = "";


    public User(){}
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.email);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.imagePath);
        dest.writeString(this.btDevice);
    }

    public void readFromParcel(Parcel in){
        this.username = in.readString();
        this.password = in.readString();
        this.email = in.readString();
        this.phoneNumber = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.imagePath = in.readString();
        this.btDevice = in.readString();
    }

    public User(Parcel in)
    {
        readFromParcel(in);
    }


    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public User createFromParcel(Parcel in) {
                    return new User(in);
                }

                public User[] newArray(int size) {
                    return new User[size];
                }
            };
}
