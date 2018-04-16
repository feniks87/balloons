package com.example.eshay.balloons.gameModule.Models;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.eshay.balloons.R;
import com.example.eshay.balloons.gameModule.Enums.BalloonEvent;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class Balloon extends Observable {
    private static int[] images = {R.drawable.blue_baloon, R.drawable.green_baloon, R.drawable.red_baloon};

    private ObjectAnimator animator;
    private ImageView balloonImageView;

    private Observer observer;

    public Balloon(AppCompatActivity activity, int maxWidth, int maxHeight, Observer mainObserver) {
        observer = mainObserver;
        final Balloon balloon = this;
        balloonImageView = new ImageView(activity);
        Random random = new Random();
        int imageIndex = random.nextInt((images.length));
        balloonImageView.setImageResource( images[imageIndex]);
        int heightPixels = 90+random.nextInt(100);

        // Converts pixels into dp
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightPixels, activity.getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(heightPixels / 2.3), activity.getResources().getDisplayMetrics());

        balloonImageView.setLayoutParams(new ConstraintLayout.LayoutParams(width, height));
        balloonImageView.setX((float) Math.floor(Math.random() * (maxWidth - width)));
        balloonImageView.setY(maxHeight);

        balloonImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    balloon.notifyObservers();
                    return true;
                } else {
                    return false;
                }
            }
        });

        animator = ObjectAnimator.ofFloat(balloonImageView, "translationY", -700f);
        animator.setDuration(5000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(final Animator animation) {

                observer.update(balloon, BalloonEvent.Pop);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public void move() {
        animator.start();
    }

    public void pause() {
        animator.pause();
    }

    public void resume() {
        animator.resume();
    }

    public ImageView getView() {
        return balloonImageView;
    }

    public void notifyObservers() {

        observer.update(this, BalloonEvent.Touched);
    }
}
