package com.mm.minesweepergo.minesweepergo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button registerBtn = (Button) findViewById(R.id.liRegisterBtn);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.liRegisterBtn:
                Intent i = new Intent(this,RegisterActivity.class);
                startActivity(i);
                break;
        }
    }
}
