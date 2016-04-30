package com.example.sergirusi.appsleep.Activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.example.sergirusi.appsleep.R;

/**
 * Created by SergiRusi on 20/12/2015.
 */
public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(11500);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    Intent openMainActivity = new Intent("android.intent.action.MAIN");
                    startActivity(openMainActivity);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
