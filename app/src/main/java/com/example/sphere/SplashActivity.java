package com.example.sphere;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.sphere.ui.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    sharedPreferences = getSharedPreferences("UserInfo",
                            Context.MODE_PRIVATE);
                    String loginStatus = sharedPreferences.getString("token", "");

                    if (loginStatus.isEmpty()) {
                        Intent m = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(m);
                        finish();
                    } else {
                        Intent m = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(m);
                        finish();
                    }
                }
            }
        };
        timer.start();
    }
}