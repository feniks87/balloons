package com.example.eshay.balloons;

import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //Screen
    private int screenWidth;
    private int screenHeight;

    private boolean isStarted = false;

    //Images
    private ImageView balloonBlue;
    private ImageView balloonRed;
    private ImageView balloonGreen;

    //Position
    private float balloonBlueX;
    private float balloonBlueY;
    private float balloonRedX;
    private float balloonRedY;
    private float balloonGreenX;
    private float balloonGreenY;

    //Initialize class
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    private boolean isTouch = false;

    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button clickButton = findViewById(R.id.startButton);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isStarted) {
                    isStarted = false;
                    Button controlButton = (Button) v;
                    controlButton.setText("Resume");
                } else {
                    isStarted = true;
                    Button controlButton = (Button) v;
                    controlButton.setText("Pause");
                }
            }
        });

        balloonBlue = (ImageView)findViewById(R.id.balloonBlue);
        balloonRed = (ImageView)findViewById(R.id.balloonRed);
        balloonGreen = (ImageView)findViewById(R.id.balloonGreen);




       //Get screen Size
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        balloonBlue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view == findViewById(R.id.balloonBlue)) {
                    view.setVisibility(View.GONE);
                    view.setY(-screenHeight -view.getHeight());
                }
                return true;
            }
        });
        balloonRed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view == findViewById(R.id.balloonRed)) {
                    view.setVisibility(View.GONE);
                    view.setY(-screenHeight -view.getHeight());
                }
                return true;
            }
        });

        //Move to out of screen
        balloonBlue.setX(-80.0f);
        balloonBlue.setY(-80.0f);
        balloonRed.setX(-40.0f);
        balloonRed.setY(-40.0f);
        balloonGreen.setX(-80.0f);
        balloonGreen.setY(-80.0f);

        //Start timer
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changePos();
                    }
                });
            }
        }, 0, 5);
    }

    public void changePos() {
        if (isStarted) {
            //Blue balloon
            balloonBlueY -= 10;
            if (balloonBlue.getY() + balloonBlue.getHeight() < 0) {
                balloonBlueX = (float) Math.floor(Math.random() * (screenWidth - balloonBlue.getWidth()));
                balloonBlueY = screenHeight + 100.0f;
                balloonBlue.setVisibility(View.VISIBLE);
            }
            balloonBlue.setX(balloonBlueX);
            balloonBlue.setY(balloonBlueY);

            //Red balloon
            balloonRedY -= 10;
            if (balloonRed.getY() + balloonRed.getHeight() < 0) {
                balloonRedX = (float) Math.floor(Math.random() * (screenWidth - balloonRed.getWidth()));
                balloonRedY = screenHeight + 100.0f;
                balloonRed.setVisibility(View.VISIBLE);
            }
            balloonRed.setX(balloonRedX);
            balloonRed.setY(balloonRedY);
        }
    }
}
