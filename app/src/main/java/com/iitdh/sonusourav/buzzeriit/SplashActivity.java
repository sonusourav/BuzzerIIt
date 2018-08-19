package com.iitdh.sonusourav.buzzeriit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {

    private ImageView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        splash =  findViewById(R.id.splash_iv);

         if(splash!=null){
             Glide.with(this)
                     .asGif()
                     .load(R.drawable.quiz_splash)
                     .into(splash)
             ;
         }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, 2000);


    }
}
