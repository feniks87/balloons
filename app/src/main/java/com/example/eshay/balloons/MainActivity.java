package com.example.eshay.balloons;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.eshay.balloons.gameModule.GameActivity;


public class MainActivity extends AppCompatActivity {

    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GameActivity.class);
                v.getContext().startActivity(intent);
                Intent myIntent = new Intent(MainActivity.this, PlayMusicService.class);
                startService(myIntent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Intent myIntent = new Intent(MainActivity.this, PlayMusicService.class);
        stopService(myIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent myIntent = new Intent(MainActivity.this, PlayMusicService.class);
        startService(myIntent);
    }
}