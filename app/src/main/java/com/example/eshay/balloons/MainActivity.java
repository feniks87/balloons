package com.example.eshay.balloons;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView balloon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        balloon = (ImageView)findViewById(R.id.balloon_blue);
        //ObjectAnimator animation;

        ObjectAnimator animation = ObjectAnimator.ofFloat(balloon, "translationX", 100);
        animation.setDuration(5000);
        animation.start();
    }


}
