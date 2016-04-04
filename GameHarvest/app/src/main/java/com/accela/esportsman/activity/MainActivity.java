package com.accela.esportsman.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.accela.esportsman.R;
import com.accela.esportsman.utils.ActivityUtils;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        View loginView= findViewById(R.id.login_layout);
        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button buttonDriverLicense = (Button) findViewById(R.id.buttonDriverLicense);
        buttonDriverLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.startActivity(MainActivity.this, RegisterActivity.class);
            }
        });

        Button buttonDECID = (Button) findViewById(R.id.buttonDecID);
        buttonDECID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                intent.putExtra("inputDecID", true);
                startActivity(intent);
            }
        });
        boolean isForRegister = getIntent().getBooleanExtra(LoginActivity.REGISTER_KEY, false);
        if(isForRegister)
            return;
        SharedPreferences prefs = getSharedPreferences(LoginActivity.LOGIN_PREFS_NAME, MODE_PRIVATE);
        String prefs_name = prefs.getString(LoginActivity.USER_NAME, null);
        if (prefs_name != null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }



}
