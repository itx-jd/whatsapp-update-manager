package com.codex.updatemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.onesignal.OneSignal;

import static com.codex.updatemanager.Constant.privacy;
import static com.codex.updatemanager.Constant.shareApp;

public class SettingActivity extends AppCompatActivity {

    Switch switch_notification,switch_smart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_setting);

        switch_notification = findViewById(R.id.switch_notification);

        switch_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    saveSwitchState(1,"Notification_Pref");
                    OneSignal.disablePush(false);
                }else{
                    saveSwitchState(0,"Notification_Pref");
                    OneSignal.disablePush(true);
                }
            }
        });

        switch_smart = findViewById(R.id.switch_smart);

        switch_smart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    saveSwitchState(1,"Smart_Pref");
                }else{
                    saveSwitchState(0,"Smart_Pref");
                }
            }
        });

        lastSwitchState();

    }

    public void lastSwitchState(){

        if(getSwitchState("Notification_Pref") == 1){
            switch_notification.setChecked(true);
        }else if (getSwitchState("Notification_Pref") == 0){
            switch_notification.setChecked(false);
        }

        if(getSwitchState("Smart_Pref") == 1){
            switch_smart.setChecked(true);
        }else if (getSwitchState("Smart_Pref") == 0){
            switch_smart.setChecked(false);
        }
    }


    public void saveSwitchState(int n,String WhichSwitch){
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(WhichSwitch, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(WhichSwitch, n);
            editor.apply();
    }

    public int getSwitchState(String WhichSwitch){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(WhichSwitch, MODE_PRIVATE);
        return sharedPreferences.getInt(WhichSwitch, 3);
    }

    public void backButton(View view) {
        onBackPressed();
    }

    public void privacy(View view) {
        startActivity(new Intent(this, PrivacyActivity.class));
    }

    @Override
    public void onBackPressed() {
        if(privacy){
            startActivity(new Intent(this,MainActivity.class));
        }else{
            super.onBackPressed();
        }
    }

    public void share(View view) {
        shareApp(this);
    }

    public void feedback(View view) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.body));

        try {
            startActivity(Intent.createChooser(intent, "send mail"));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "E-mail App Not Found !", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "Unexpected Error !", Toast.LENGTH_SHORT).show();
        }
    }

}