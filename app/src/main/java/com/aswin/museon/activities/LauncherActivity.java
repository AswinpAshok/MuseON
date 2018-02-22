package com.aswin.museon.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aswin.museon.R;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        SharedPreferences preferences=getSharedPreferences("myPrefs",MODE_PRIVATE);
        String username=preferences.getString("username","");
        if(!username.equals("")){
            finish();
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }else {
            finish();
            Intent intent=new Intent(this,SignUpActivity.class);
            startActivity(intent);
        }
    }
}
