package com.example.eshay.balloons.gameModule;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.example.eshay.balloons.R;
import com.example.eshay.balloons.gameModule.Enums.GameState;
import com.example.eshay.balloons.gameModule.Models.Game;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    private int screenWidth;
    private int screenHeight;

    private Timer balloonCreator = new Timer();

    private CountDownTimer countDownTimer;

    private long gameDuration = 30000;

    private long timeBuffer = gameDuration;

    Animation timerAnimation;

    private Game game;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initScreenSize();
        game = new Game(this, screenWidth, screenHeight);

        mediaPlayer = MediaPlayer.create(this, R.raw.background_2);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        setStartButton();

        // Start timer for creating baloons
        balloonCreator.schedule(new TimerTask() {
            @Override
            public void run() {
                if (game.getState() == GameState.Started) {
                    for (int i = 0; i < new Random().nextInt(3)+ 2; i++) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                game.addBalloon();
                            }
                        });
                    }
                }
            }
        }, 0, 1000);

        // Animation for Timer text
        timerAnimation = new AlphaAnimation(0.0f, 1.0f);
        timerAnimation.setDuration(1000);
        timerAnimation.setStartOffset(20);
        timerAnimation.setRepeatMode(Animation.REVERSE);
        timerAnimation.setRepeatCount(Animation.INFINITE);
    }

    private void setStartButton() {
        Button clickButton = findViewById(R.id.startButton);

        clickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (game.getState() == GameState.Finished) {
                    timeBuffer = gameDuration;
                }

                game.changeState();
                Button controlButton = (Button) v;
                switch (game.getState()) {
                    case Paused:
                        controlButton.setText("Resume");
                        countDownTimer.cancel();
                        break;
                    case Started:
                        final TextView timeValue = findViewById(R.id.timerValue);
                        timerAnimation.cancel();
                        timeValue.clearAnimation();
                        countDownTimer = new CountDownTimer(timeBuffer, 1000) {
                            public void onTick(long millisUntilFinished) {
                                timeBuffer = millisUntilFinished;
                                int secs = (int) (millisUntilFinished / 1000);
                                int mins = secs / 60;
                                secs = secs % 60;
                                timeValue.setText("" + mins + ":" + String.format("%02d", secs));
                            }

                            public void onFinish() {
                                Button clickButton = findViewById(R.id.startButton);
                                timeValue.setText("Time is up!");
                                timeValue.startAnimation(timerAnimation);
                                clickButton.setText("New");
                                game.finishGame();
                            }
                        }.start();

                        controlButton.setText("Pause");
                        TextView scoreText = findViewById(R.id.scoreText);
                        scoreText.setText("Score: " + game.getScore());
                        break;
                }
            }
        });
    }

    private void initScreenSize() {
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mediaPlayer != null && !mediaPlayer.isPlaying())
            mediaPlayer.start();
    }
}