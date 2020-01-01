package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashAcitvity extends AppCompatActivity {

    private ImageView logo;
    private static int splashTimeOut=3000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo=(ImageView)findViewById(R.id.app_logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainActivity = new Intent(SplashAcitvity.this,MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        },splashTimeOut);

        Animation anim= AnimationUtils.loadAnimation(this,R.anim.splashanim);
        logo.startAnimation(anim);
    }
}
