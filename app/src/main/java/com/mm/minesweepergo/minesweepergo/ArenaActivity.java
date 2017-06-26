package com.mm.minesweepergo.minesweepergo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mm.minesweepergo.minesweepergo.DomainModel.Arena;
import com.mm.minesweepergo.minesweepergo.DomainModel.Game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mm.minesweepergo.minesweepergo.R.id.aActiveGames;

public class ArenaActivity extends AppCompatActivity implements View.OnClickListener {

    public Arena arena;
    ArrayAdapter<String> adapterGames;
    ArrayList<String> gamesList;
    List<Game> allGames;

    ListView games;
    String myUsername;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aAddGame:
                Intent i = new Intent(ArenaActivity.this, MinesSetActivity.class);
                i.putExtra("arena", this.arena);

                try {
                    startActivity(i);
                }catch(Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena);

        SharedPreferences sharedPref = this.getSharedPreferences(
                "UserInfo", Context.MODE_PRIVATE);

        myUsername = sharedPref.getString("Username", "empty");

        Button addGame = (Button) findViewById(R.id.aAddGame);
        addGame.setOnClickListener(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.arena_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        gamesList = new ArrayList<>();

        adapterGames = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,gamesList);

        games = (ListView) findViewById(R.id.aActiveGames);
        games.setAdapter(adapterGames);

        games.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String username = allGames.get(position).getCreatorUsername();
                int gameId = allGames.get(position).getId();

                if(myUsername.equals(username))
                {
                    Toast.makeText(ArenaActivity.this, "Can only play games created by other users.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent(ArenaActivity.this,MinesSearchActivity.class);
                i.putExtra("username",myUsername);
                i.putExtra("gameId",gameId);
                i.putExtra("arena", arena);

                startActivity(i);
            }
        });


        arena = new Arena();

        Intent i = getIntent();
        Bundle bndl = i.getExtras();
        arena.name = bndl.getString("arenaName");
        arena.radius = bndl.getDouble("arenaRadius");
        arena.centerLat = bndl.getDouble("centerLat");
        arena.centerLon = bndl.getDouble("centerLon");

        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    allGames = HTTP.getArenaGames(arena.name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        transThread.shutdown();
        try {
            transThread.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.SECONDS);

        } catch (InterruptedException E) {
            // handle
        }

        for(int j=0; j<allGames.size(); j++)
        {
            gamesList.add(allGames.get(j).getId() + " \t\t\t\t\t\t " + allGames.get(j).getCreatorUsername());
        }
        adapterGames.notifyDataSetChanged();

        TextView name = (TextView) findViewById(R.id.aName);
        name.setText(arena.name);
        TextView area = (TextView) findViewById(R.id.aArea);
        area.setText(new DecimalFormat("#.##").format(arena.radius * arena.radius * Math.PI) + " m^2");
        TextView latLong = (TextView) findViewById(R.id.aCoords);
        latLong.setText(arena.centerLat+ " \t\t " + arena.centerLon);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
