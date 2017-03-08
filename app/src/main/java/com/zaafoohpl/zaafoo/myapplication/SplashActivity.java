package com.zaafoohpl.zaafoo.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class SplashActivity extends AppCompatActivity {


    SessionManagement sm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sm = new SessionManagement(this);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        TextView name = (TextView) findViewById(R.id.textView5);
        name.setText("Zaafoo");
        name.setTypeface(face);


        TextView slogan = (TextView) findViewById(R.id.textView6);
        slogan.setText("Your Wish,Your Choice");
        slogan.setTypeface(face);


        if(sm.checkToken())
            startActivity(new Intent(SplashActivity.this,GetLocation.class));
        else
            startActivity(new Intent(SplashActivity.this,MainActivity.class));

        finish();

    }
}
