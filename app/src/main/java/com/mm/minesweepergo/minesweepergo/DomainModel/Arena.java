package com.mm.minesweepergo.minesweepergo.DomainModel;

/**
 * Created by Milos on 6/9/2017.
 */

public class Arena {
    public double centerLat;
    public double centerLon;
    public double radius;
    public String name;

    public Arena (){}

    public Arena( double lat, double lon, double rad, String name){
        this.centerLat = lat;
        this.centerLon = lon;
        this.radius = rad;
        this.name  = name;
    }

}
