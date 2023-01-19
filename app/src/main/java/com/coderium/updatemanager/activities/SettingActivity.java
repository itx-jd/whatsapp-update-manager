package com.coderium.updatemanager.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.coderium.updatemanager.R;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.onesignal.OneSignal;

import static com.coderium.updatemanager.utilities.Constant.downloadFileFromURL;
import static com.coderium.updatemanager.utilities.Constant.shareApp;
import static com.coderium.updatemanager.utilities.Constant.updateDialogNotVisible;
import static com.coderium.updatemanager.utilities.Constant.waUpdateManagerList;

public class SettingActivity extends AppCompatActivity {

    Switch switch_notification,switch_smart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_setting);

        switch_notification = findViewById(R.id.switch_notification);
        switch_smart = findViewById(R.id.switch_smart);

        // Fetching the Old Configure Settings
        lastSwitchState();

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
        return sharedPreferences.getInt(WhichSwitch, 1);
    }

    public void backButton(View view) {
        onBackPressed();
    }

    public void privacy(View view) {
        startActivity(new Intent(this, PrivacyActivity.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
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
            Toast.makeText(this, "E-mail App Not Found \uD83D\uDE10", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "Unexpected Error \uD83D\uDE10", Toast.LENGTH_SHORT).show();
        }
    }

    public void update(View view) {

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);

            if(waUpdateManagerList!=null){
                if(!packageInfo.versionName.equalsIgnoreCase(waUpdateManagerList.get(1))){
                    updateDialog();
                }else{
                    Toast.makeText(this, "You Have Latest Version", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Good Job \uD83D\uDC4D", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(SettingActivity.this, getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        
    }

    private void updateDialog() {

        Button btn_no, btn_update;
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(SettingActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.update_dialog, viewGroup, false);
        materialAlertDialogBuilder.setCancelable(false);
        materialAlertDialogBuilder.setView(dialogView);
        AlertDialog dialog = materialAlertDialogBuilder.show();
        updateDialogNotVisible = false;

        btn_no = dialogView.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


        btn_update = dialogView.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                downloadFileFromURL(SettingActivity.this,waUpdateManagerList.get(4),"WA Update Manager",waUpdateManagerList.get(1));
                Toast.makeText(SettingActivity.this, getString(R.string.toast_download), Toast.LENGTH_SHORT).show();
                Toast.makeText(SettingActivity.this, "Good Job \uD83D\uDC4D", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void contribute(View view) {
        startActivity(new Intent(this, ContributorActivity.class));
    }

    public void donate(View view) {

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(waUpdateManagerList.get(5)));
            startActivity(browserIntent);
        }catch (Exception ex){
            Toast.makeText(SettingActivity.this, getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();
        }
    }
}