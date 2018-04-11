package com.example.eshay.balloons.gameModule;

import android.graphics.Point;
import android.media.MediaPlayer;
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

import com.example.eshay.balloons.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    public enum GameState {
        New, Started, Paused, Finished
    }

    //Screen
    private int screenWidth;
    private int screenHeight;

    private GameState gameState = GameState.New;

    //Initialize class
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    private Timer balloonCreator = new Timer();

    private CountDownTimer countDownTimer;

    private int counter;

    private long gameDuration = 30000;

    private long timeBuffer = gameDuration;

    private static int[] images = {R.drawable.blue_baloon, R.drawable.green_baloon, R.drawable.red_baloon};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initScreenSize();
        setStartButton();

        // Start timer for creating baloons
        balloonCreator.schedule(new TimerTask() {
            @Override
            public void run() {
                if (gameState == GameState.Started) {
                    for (int i = 0; i < new Random().nextInt(3)+ 2; i++) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView balloon = createBalloonImageView();
                                ConstraintLayout mainLayout = (ConstraintLayout)findViewById(R.id.current_layout);
                                mainLayout.addView(balloon);
                            }
                        });
                    }
                }
            }
        }, 0, 1000);

        //Start timer for changing balloons position
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
        }, 0, 15);
    }

    private void setStartButton() {
        Button clickButton = findViewById(R.id.startButton);


        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final TextView timeValue = findViewById(R.id.timerValue);
                switch (gameState) {

                    case Started:
                        gameState = GameState.Paused;
                        Button controlButton = (Button) v;
                        controlButton.setText("Resume");
                        countDownTimer.cancel();
                        break;
                    case Finished:
                        timeBuffer = gameDuration;
                        clearBalloons();
                        counter = 0;
                        TextView scoreText = (TextView)findViewById(R.id.scoreText);
                        scoreText.setText("Score: "+counter);
                    case Paused: case New:
                        gameState = GameState.Started;
                        Button button = (Button) v;
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
                                gameState = GameState.Finished;
                                clickButton.setText("New");
                                clearBalloons();
                            }
                        } .start();

                        button.setText("Pause");
                        break;
                }
            }
        });
    }
    //Clears balloons before starting game
    private void clearBalloons() {
        boolean doClear = false;
        ConstraintLayout mainLayout = (ConstraintLayout)findViewById(R.id.current_layout);
        while (!doClear) {
            int childCount = mainLayout.getChildCount();
            int i;
            for(i=0; i<childCount; i++) {
                View currentChild = mainLayout.getChildAt(i);
                // Change ImageView with desired type view
                if (currentChild instanceof ImageView) {
                    mainLayout.removeView(currentChild);
                    break;
                }
            }

            if (i == childCount) {
                doClear = true;
            }
        }
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
        Random random = new Random();
        int imageIndex = random.nextInt((images.length));
        balloon.setImageResource( images[imageIndex]);
        int heightPixels = 90+random.nextInt(100);

        // converts pixels into dp
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightPixels, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(heightPixels / 2.3), getResources().getDisplayMetrics());

        balloon.setLayoutParams(new ConstraintLayout.LayoutParams(width, height));
        balloon.setX((float) Math.floor(Math.random() * (screenWidth - width)));
        balloon.setY(screenHeight + height);

        balloon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && gameState == GameState.Started) {
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
    MediaPlayer balloonMP;
    // Removes balloons when touched
    private void onBalloonTouch(ImageView view) {
        counter++;
        final ImageView toRemove = view;
        TextView scoreText = (TextView)findViewById(R.id.scoreText);

        balloonMP = MediaPlayer.create(this, R.raw.balloon_pop);
        balloonMP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.reset();
                mp.release();
            }

        });
        scoreText.setText("Score: "+counter);
        handler.post(new Runnable() {
            @Override
            public void run() {
                ConstraintLayout mainLayout = (ConstraintLayout)findViewById(R.id.current_layout);
                mainLayout.removeView(toRemove);
                balloonMP.start();
            }
        });
    }

    public void changePos() {
        if (gameState == GameState.Started) {
            ConstraintLayout mainLayout = (ConstraintLayout)findViewById(R.id.current_layout);
            for (int i = 0; i < mainLayout.getChildCount(); i++) {

                final View subView = mainLayout.getChildAt(i);

                if (subView instanceof ImageView) {
                    float y = subView.getY() - 15;
                    if (subView.getY() + subView.getHeight() < 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ConstraintLayout mainLayout = (ConstraintLayout)findViewById(R.id.current_layout);
                                mainLayout.removeView(subView);
                            }
                        });
                    }

                    subView.setY(y);
                }
            }
        }
    }
}