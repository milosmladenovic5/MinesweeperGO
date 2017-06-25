package com.mm.minesweepergo.minesweepergo.DomainModel;

import java.util.List;

/**
 * Created by Milos on 6/10/2017.
 */

public class Game {
    private int id;
    private String creatorUsername;
    private List<Mine> mines;

    public int getId() {return this.id;}
    public void setId(int id) {this.id = id;}
    public String getCreatorUsername() {return this.creatorUsername;}
    public void setCreatorUsername(String creatorUsername) {this.creatorUsername = creatorUsername;}

    public void addMine(Mine m) {this.mines.add(m);}
    public List<Mine> getMines() {return this.mines;}

}
