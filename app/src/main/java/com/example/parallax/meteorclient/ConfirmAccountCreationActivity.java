package com.example.parallax.meteorclient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.ResultListener;

public class ConfirmAccountCreationActivity extends AppCompatActivity {

    public static final int ACCEPTED = 1;
    public static final int DENIED   = 2;

    String username;
    String password;

    Meteor meteor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_account_creation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        MainApplication app = (MainApplication)getApplication();
        meteor = app.getMeteor();
    }

    public void createAccount(View view) {
        meteor.registerAndLogin(username, null,  password, new ResultListener() {
            @Override
            public void onSuccess(String result) {
                setResult(ACCEPTED);
                finish();
            }

            @Override
            public void onError(String error, String reason, String details) {
                setResult(DENIED);
                finish();
            }
        });
    }

    public void cancel(View view)
    {
        setResult(DENIED);
        finish();;
    }

}
