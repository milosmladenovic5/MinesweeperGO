package com.mm.minesweepergo.minesweepergo.DomainModel;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.mm.minesweepergo.minesweepergo.Utilities;

/**
 * Created by Milos on 6/9/2017.
 */

public class Arena implements Parcelable {
    public double centerLat;
    public double centerLon;
    public double radius;
    public String name;

    public Arena (){}
    public Arena(Parcel in)
    {
        this.readFromParcel(in);
    }

    public Arena( double lat, double lon, double rad, String name){
        this.centerLat = lat;
        this.centerLon = lon;
        this.radius = rad;
        this.name  = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeDouble(this.centerLat);
        dest.writeDouble(this.centerLon);
        dest.writeDouble(this.radius);

    }

    public void readFromParcel(Parcel in){
        this.name = in.readString();
        this.centerLat = in.readDouble();
        this.centerLon = in.readDouble();
        this.radius = in.readDouble();
    }

    public boolean outsideArena(Location location)
    {
        return Utilities.distance(location.getLatitude(),location.getLongitude(),this.centerLat,this.centerLon, false)> this.radius;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Arena createFromParcel(Parcel in) {
                    return new Arena(in);
                }

                public Arena[] newArray(int size) {
                    return new Arena[size];
                }
            };

}
