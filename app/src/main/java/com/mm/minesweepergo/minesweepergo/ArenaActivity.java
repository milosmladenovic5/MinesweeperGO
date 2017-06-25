package com.mm.minesweepergo.minesweepergo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aAddGame:
                // startuj intent
                // prosledi mu arenu za koje se pravi novi GEJM
                Intent i = new Intent(ArenaActivity.this, MinesSetActivity.class);
                i.putExtra("arena", this.arena);

                try {
                    startActivity(i);
                }catch(Exception e) {
                    Log.e("ha", "ha");
                }

                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena);

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
                String itemText = (String) parent.getItemAtPosition(position);
                String [] parts = itemText.split("|");
                String username = parts[1];
                String gameId = parts[0];

                Toast.makeText(ArenaActivity.this, "Username" + username + "\t game id je " + gameId, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(ArenaActivity.this,MinesSearchActivity.class);
                i.putExtra("username",username);
                i.putExtra("gameId",Integer.parseInt(gameId));

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
            gamesList.add(allGames.get(j).getId() + "|" + allGames.get(j).getCreatorUsername());
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
