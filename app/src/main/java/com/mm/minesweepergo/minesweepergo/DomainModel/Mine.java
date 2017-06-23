package com.mm.minesweepergo.minesweepergo.DomainModel;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.mm.minesweepergo.minesweepergo.Utilities;

/**
 * Created by Milan Nikolić on 22-Jun-17.
 */

public class Mine {

    private LatLng location;
    private double blastRadius;
    private boolean found = false;


    public Mine() {}
    public Mine(LatLng location, double blastRadius) {
        this.location = location;
        this.blastRadius = blastRadius;
    }
    public void setLocation(LatLng location){this.location = location;}
    public LatLng getLocation() {return this.location;}
    public void setBlastRadius(double br) {this.blastRadius = br;}
    public double getBlastRadius() {return this.blastRadius;}
    public boolean isFound() {return this.found;}
    public void flagMine() {this.found = true /* kad jednom stavi zastavicu NE moze da je  dira - sorry */;}

    public int isNearby(Location userLocation, double scannerRadius){ // za sad smo odlucili da bidne fiksan ili da zavisi od poena
        //  ako je usao u blastRadius i pritisnuo scan - mrtav je
        //  ako je mina u "rasponu (in range - wtf )" skenera(scannerRadius) vraca se true (i inkremenitra broj koji se prikazuje na mapi)

        //  razdaljina u metrima
        double distance = Utilities.distance(userLocation.getLatitude(), userLocation.getLongitude(),this.location.latitude, this.location.longitude, false );

        if(this.found) return 1; // nista
        else if (distance < this.blastRadius ) return -1; //   mrtav
        else if(distance < scannerRadius) return 0; //   nasao je
        else return 1; //   nista

    }
}
