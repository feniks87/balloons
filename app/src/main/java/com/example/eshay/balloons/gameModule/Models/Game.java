package com.example.eshay.balloons.gameModule.Models;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eshay.balloons.R;
import com.example.eshay.balloons.gameModule.Enums.BalloonEvent;
import com.example.eshay.balloons.gameModule.Enums.GameState;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Game implements Observer {
    private GameState gameState = GameState.New;

    private ArrayList<Balloon> balloons = new ArrayList<>();

    private int screenWidth;
    private int screenHeight;
    private MediaPlayer balloonMP;
    private int score;
    private Handler handler = new Handler();
    private AppCompatActivity activity;

    public Game(AppCompatActivity mainActivity, int width, int height) {
        activity = mainActivity;
        screenHeight = height;
        screenWidth = width;
    }

    public GameState getState() {
        return gameState;
    }

    public int getScore() {
        return score;
    }

    public void addBalloon() {
        Balloon balloon = new Balloon(activity, screenWidth, screenHeight, this);
        final ImageView toAdd = balloon.getView();
        balloons.add(balloon);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConstraintLayout mainLayout = activity.findViewById(R.id.current_layout);
                mainLayout.addView(toAdd);
            }
        });
        balloon.move();
    }

    @Override
    public void update(Observable observable, Object o) {
        Balloon balloon = (Balloon) observable;
        BalloonEvent event = (BalloonEvent) o;
        final ImageView toRemove = balloon.getView();
        switch (event) {
            case Pop:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ConstraintLayout mainLayout = activity.findViewById(R.id.current_layout);
                        mainLayout.removeView(toRemove);
                    }
                });
                balloons.remove(balloon);
                break;
            case Touched:
                if (gameState == GameState.Started) {
                    score++;

                    TextView scoreText = activity.findViewById(R.id.scoreText);

                    balloonMP = MediaPlayer.create(activity, R.raw.balloon_pop);
                    balloonMP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.reset();
                            mp.release();
                        }

                    });
                    scoreText.setText("Score: " + score);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ConstraintLayout mainLayout = activity.findViewById(R.id.current_layout);
                            mainLayout.removeView(toRemove);
                            balloonMP.start();
                        }
                    });
                }
                break;
        }
    }

    public void changeState() {
        switch (gameState) {
            case Started:
                gameState = GameState.Paused;
                for (Balloon balloon : balloons) {
                    balloon.pause();
                }
                break;
            case Finished:
                gameState = GameState.New;
                score = 0;
            case Paused:
                gameState = GameState.Started;
                for (Balloon balloon : balloons) {
                    balloon.resume();
                }
            case New:
                gameState = GameState.Started;
                break;
        }
    }

    public void finishGame() {
        gameState = GameState.Finished;
        cleareBalloons();
    }

    private void cleareBalloons() {
        ConstraintLayout mainLayout = activity.findViewById(R.id.current_layout);
        for (Balloon balloon : balloons) {
            mainLayout.removeView(balloon.getView());
        }
        balloons.clear();
    }
}
