package com.example.petwalk;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petwalk.utilerias.SessionManager;

public class Splash_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_);

        SessionManager sm = new SessionManager(getApplicationContext());
        sm.checkLogin();

    }

}
