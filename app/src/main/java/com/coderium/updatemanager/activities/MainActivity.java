package com.coderium.updatemanager.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coderium.updatemanager.R;
import com.coderium.updatemanager.adapters.APKAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.onesignal.OneSignal;

import java.util.List;
import java.util.Map;

import static com.coderium.updatemanager.utilities.Constant.apkUrlList;
import static com.coderium.updatemanager.utilities.Constant.contactDataList;
import static com.coderium.updatemanager.utilities.Constant.downloadFileFromURL;
import static com.coderium.updatemanager.utilities.Constant.downloadStatsList;
import static com.coderium.updatemanager.utilities.Constant.releaseVersionList;
import static com.coderium.updatemanager.utilities.Constant.shareApp;
import static com.coderium.updatemanager.utilities.Constant.updateDialogNotVisible;
import static com.coderium.updatemanager.utilities.Constant.waUpdateManagerList;
import static com.coderium.updatemanager.utilities.Contact.LinkedInIntent;
import static com.coderium.updatemanager.utilities.Contact.facebookIntent;
import static com.coderium.updatemanager.utilities.Contact.githubIntent;
import static com.coderium.updatemanager.utilities.Contact.hireIntent;
import static com.coderium.updatemanager.utilities.Contact.instagramIntent;
import static com.coderium.updatemanager.utilities.Contact.snapchatIntent;
import static com.coderium.updatemanager.utilities.Contact.telegramIntent;
import static com.coderium.updatemanager.utilities.Contact.twitterIntent;
import static com.coderium.updatemanager.utilities.Contact.websiteIntent;


public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAnalytics firebaseAnalytics;
    RecyclerView recycleView;
    int back = 0;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This code is for customize top bar
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.tollbar);
        setSupportActionBar(toolbar);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("ee52a2e7-b1ac-4ffc-9555-8cfa3fded287");

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // setting up swipe refresh layout

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh();
            }
        });

        // setting up recycle view

        recycleView = findViewById(R.id.recycleView);
        recycleAdapter();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Calling Function that fetch data from firebase
        fetchingData();

    }

    public void fetchingData(){

        // Fetching core purpose data from firebase

        getDataFromFirebase("Release_Version",releaseVersionList);
        getDataFromFirebase("links",apkUrlList);

        // Fetching the data related to downloaded items stats and app share text and links
        getDataFromFirebase("stats/mods/",downloadStatsList);
        getDataFromFirebase("contact",contactDataList);
        getDataFromFirebase("WA Update Manager",waUpdateManagerList);

    }

    public void recycleAdapter(){
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        APKAdapter APKAdapter = new APKAdapter(this,getParent());
        recycleView.setAdapter(APKAdapter);
    }

    public void getDataFromFirebase(String path, List<String> listName){

        databaseReference.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listName.clear();
                Map<String, Object> data = (Map<String, Object>) snapshot.getValue();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    String key = snapshot1.getKey();
                    String value = (String) data.get(key);
                    listName.add(value);
                }

                //  calling recycle adapter to display the list of items on screen cuz "Release_Version" and "Links" has been fetched
                if(path.equalsIgnoreCase("links")){
                    recycleAdapter();
                }
                // It is calling here because It trigger before fetching data even writing after the fetching line because of delay while fetching data
                if(path.equalsIgnoreCase("WA Update Manager")){
                    checkForUpdates();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"Oops! Something went wrong \uD83D\uDE2D", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void checkForUpdates(){

        // Handler will give 4 seconds delay to provide smooth experience
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
                    if(!packageInfo.versionName.equalsIgnoreCase(waUpdateManagerList.get(1))){
                        updateDialog();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }, 4000);

    }

    public void swipeRefresh(){

        // Handler will provide 2 seconds delay to give smooth experience cuz data is soo small and fetching too quickly.
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                fetchingData();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                shareApp(this);
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.about:
                aboutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void aboutDialog() {
        Button btn_portfolio, btn_hireMe;
        ImageView iv_cancle;
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.about_dialog, viewGroup, false);
        materialAlertDialogBuilder.setCancelable(false);
        materialAlertDialogBuilder.setView(dialogView);
        AlertDialog dialog = materialAlertDialogBuilder.show();

        btn_portfolio = dialogView.findViewById(R.id.btn_portfolio);
        btn_portfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                githubIntent(MainActivity.this);
            }
        });


        btn_hireMe = dialogView.findViewById(R.id.btn_hireMe);
        btn_hireMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
                hireIntent(MainActivity.this);
            }
        });

        iv_cancle = dialogView.findViewById(R.id.iv_cancle);
        iv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

    }

    private void updateDialog() {

        Button btn_no, btn_update;
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this);
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
                downloadFileFromURL(MainActivity.this,waUpdateManagerList.get(4),"WA Update Manager",waUpdateManagerList.get(1));
                Toast.makeText(MainActivity.this, getString(R.string.toast_download), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (back == 0) {
            Toast.makeText(this, "Press Again To Exit", Toast.LENGTH_SHORT).show();
            back = 1;
        } else {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            finish();
        }
    }

    // Social Media Methods

    public void instagram(View view) {
        instagramIntent(this);
    }

    public void snapchat(View view) {
        snapchatIntent(this);
    }

    public void github(View view) {
        githubIntent(this);
    }

    public void twitter(View view) {
        twitterIntent(this);
    }

    public void linkedIn(View view) {
        LinkedInIntent(this);
    }

    public void telegram(View view) {
        telegramIntent(this);
    }

}