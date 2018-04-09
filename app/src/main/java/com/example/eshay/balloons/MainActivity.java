package com.example.eshay.balloons;

import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //Screen
    private int screenWidth;
    private int screenHeight;

    private boolean isStarted = false;

    //Initialize class
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    private CountDownTimer countDownTimer;
    private int counter;

    private long timeBuffer = 30000;

    private static int[] images = {R.drawable.blue_baloon, R.drawable.green_baloon, R.drawable.red_baloon};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get screen Size
        initScreenSize();

        setStartButton();

        ConstraintLayout mainLayout = (ConstraintLayout)findViewById(R.id.current_layout);
        for (int i = 0; i < 5; i++) {
            ImageView balloon = createBalloonImageView();
            mainLayout.addView(balloon);
        }

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
        }, 0, 25);
    }

    private void setStartButton() {
        Button clickButton = findViewById(R.id.startButton);


        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final TextView timeValue = findViewById(R.id.timerValue);

                if (isStarted) {
                    isStarted = false;
                    Button controlButton = (Button) v;
                    controlButton.setText("Resume");
                    countDownTimer.cancel();

                } else {
                    isStarted = true;
                    countDownTimer = new CountDownTimer(timeBuffer, 1000) {
                        public void onTick(long millisUntilFinished) {
                            timeBuffer = millisUntilFinished;
                            int secs = (int) (millisUntilFinished / 1000);
                            int mins = secs / 60;
                            secs = secs % 60;
                            timeValue.setText("" + mins + ":" + String.format("%02d", secs));
                        }

                        public void onFinish() {
                            timeValue.setText("Time is up!");
                            isStarted = false;
                        }
                    } .start();

                    Button controlButton = (Button) v;
                    controlButton.setText("Pause");
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

    private ImageView createBalloonImageView() {
        ImageView balloon = new ImageView(this);
        int imageIndex = new Random().nextInt((images.length));
        balloon.setImageResource( images[imageIndex]);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 186, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 81, getResources().getDisplayMetrics());
        balloon.setLayoutParams(new ConstraintLayout.LayoutParams(width, height));
        balloon.setX((float) Math.floor(Math.random() * (screenWidth - balloon.getWidth())));
        balloon.setY(Integer.MIN_VALUE);
        balloon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onBalloonTouch((ImageView)view);
                    return true;
                }
                else {
                    return false;
                }
            }
        });
        return balloon;
    }

    private void onBalloonTouch(ImageView view) {
        view.setVisibility(View.GONE);
        view.setY(Integer.MIN_VALUE);
        counter++;
        TextView scoreText = (TextView)findViewById(R.id.scoreText);
        scoreText.setText("Score: "+counter);
    }

    public void changePos() {
        if (isStarted) {
            ConstraintLayout mainLayout = (ConstraintLayout)findViewById(R.id.current_layout);
            for (int i = 0; i < mainLayout .getChildCount(); i++) {

                View subView = mainLayout.getChildAt(i);

                if (subView instanceof ImageView) {
                    float y = subView.getY() - 5;
                    if (subView.getY() + subView.getHeight() < 0) {
                        float x = (float) Math.floor(Math.random() * (screenWidth - subView.getWidth()));
                        y = screenHeight + 100.0f;
                        subView.setVisibility(View.VISIBLE);
                        subView.setX(x);
                    }

                    subView.setY(y);
                }
            }
        }
    }
}
