package com.example.eshay.balloons;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.eshay.balloons.gameModule.GameActivity;

public class MainActivity extends AppCompatActivity {

    private Button playButton;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = MediaPlayer.create(this, R.raw.background);

        playButton = (Button) findViewById(R.id.playButton);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GameActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaPlayer.start();
    }
}