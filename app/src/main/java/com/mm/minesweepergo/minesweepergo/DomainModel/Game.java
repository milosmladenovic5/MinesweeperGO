package com.mm.minesweepergo.minesweepergo.DomainModel;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Milos on 6/10/2017.
 */

public class Game {
    private int id;
    private String creatorUsername;
    private List<Mine> mines;
    private int flagedCount = 0;

    public int getFlagedCount(){return flagedCount;}

    public int score() {return mines.size() - flagedCount;}

    public int getId() {return this.id;}
    public void setId(int id) {this.id = id;}
    public String getCreatorUsername() {return this.creatorUsername;}
    public void setCreatorUsername(String creatorUsername) {this.creatorUsername = creatorUsername;}

    public void addMine(Mine m) {this.mines.add(m);}

    public List<Mine> getMines() {return this.mines;}



    public Game(){
        mines = new ArrayList<>();


    };

    public int scan(Location userLocation, double scanRadius){

        int result = 0; //  broj minica u okolini (onaj koji crtamo na mapi na trenutnoj lokaciji korisnika)
        for (Iterator<Mine> mineIterator = this.mines.iterator(); mineIterator.hasNext();)
        {
            Mine mine = mineIterator.next();
            int val = mine.isNearby(userLocation, scanRadius); // 1 - nije u blizini ili je demontirana, 0 - u okviru radijusa, -1 BUM

            if(val == -1) return -1; // gameover
            else if(val == 0) result++; //  mina je u okviru scanRadius~ inkrementiramo rezultat

        }
        return result;
    }

    public boolean flag(Location userLocation){

        for (Iterator<Mine> mineIterator = this.mines.iterator(); mineIterator.hasNext();)
        {
            Mine mine = mineIterator.next();
            //  prva varijanta
            //  vrednost 0 za skan - u ovom slucaju proveravamo samo da li je usao u blastRadius mine
            //  ako isNearby:int vrati -1 mina flagovana
            int val = mine.isNearby(userLocation, 0);

            // usao u blast radius i postavio flag - mina demontirana d(-_-)b
            if(val == -1) {mine.flagMine(); this.flagedCount++;}

        }
        return this.flagedCount == this.mines.size(); // true = pobedio, sve mine pronadjene..
    }

    public boolean flag(Location userLocation, double scanRadius){

        for (Iterator<Mine> mineIterator = this.mines.iterator(); mineIterator.hasNext();)
        {
            Mine mine = mineIterator.next();
            //  druga varijanta
            //  neka vrednost za skan (ne mora da udje u blastRadius da je demontira)
            //  ako isNearby:int vrati 0 znaci da je u scanRadius-u i da je uspesno demontirana/flagovana
            int val = mine.isNearby(userLocation, scanRadius);

            // usao u blast radius i postavio flag - mina demontirana d(-_-)b
            if(val == 0) {mine.flagMine(); this.flagedCount++;}

        }
        return this.flagedCount == this.mines.size(); // true = pobedio, sve mine pronadjene..
    }



}
