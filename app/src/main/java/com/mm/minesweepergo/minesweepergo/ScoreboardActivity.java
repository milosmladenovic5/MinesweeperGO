package com.mm.minesweepergo.minesweepergo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScoreboardActivity extends AppCompatActivity {

    ArrayList<String> scores;
    ArrayAdapter<String> adapterScores;

    ListView scoreboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.scoreboard_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        scores = new ArrayList<>();

        adapterScores = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, scores);
        scoreboard = (ListView) findViewById(R.id.scoreboard_list_view);

        scoreboard.setAdapter(adapterScores);

        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<String> tmp = HTTP.getScoreboard();
                    for(int i = 0; i < tmp.size();i++)
                        scores.add(tmp.get(i));
                    adapterScores.notifyDataSetChanged();

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
        adapterScores.notifyDataSetChanged();

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
