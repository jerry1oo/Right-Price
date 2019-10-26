package com.example.rightprice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

//Class for displaying the splash screen when the app starts
public class SplashActivity extends AppCompatActivity {



    /*
    This method is the first activity in our application
    It displays our app logo, and then launches our login activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
