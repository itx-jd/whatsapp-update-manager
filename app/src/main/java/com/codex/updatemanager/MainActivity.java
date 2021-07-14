package com.codex.updatemanager;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.onesignal.OneSignal;
import java.util.Map;

import static com.codex.updatemanager.Constant.apkUrls;
import static com.codex.updatemanager.Constant.contactDataList;
import static com.codex.updatemanager.Constant.downloadFile;
import static com.codex.updatemanager.Constant.installedVersion;
import static com.codex.updatemanager.Constant.internetAvailable;
import static com.codex.updatemanager.Constant.internetIsConnected;
import static com.codex.updatemanager.Constant.isConnected;
import static com.codex.updatemanager.Constant.latestVersion;
import static com.codex.updatemanager.Constant.packageName;
import static com.codex.updatemanager.Constant.readableVersion;
import static com.codex.updatemanager.Constant.shareApp;
import static com.codex.updatemanager.Constant.updateAppList;
import static com.codex.updatemanager.Constant.updateDialogNotVisible;
import static com.codex.updatemanager.Constant.dataReady;
import static com.codex.updatemanager.Contact.facebookIntent;
import static com.codex.updatemanager.Contact.githubIntent;
import static com.codex.updatemanager.Contact.hireIntent;
import static com.codex.updatemanager.Contact.instagramIntent;
import static com.codex.updatemanager.Contact.snapchatIntent;
import static com.codex.updatemanager.Contact.websiteIntent;
import static com.codex.updatemanager.Constant.privacy;


public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView recycleView;
    int back = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.tollbar);
        setSupportActionBar(toolbar);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("ee52a2e7-b1ac-4ffc-9555-8cfa3fded287");


        // I have back issue with privacy activity and setting activity
        privacy = false;


        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh();
            }
        });

        recycleView = findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleAdapter();

        if(!dataReady){
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            getContactData();
            getInstalledAppsVersion();
            getFirebaseData();
        }

        checkForUpdate();
    }

    public void swipeRefresh(){
        internetAvailable = isConnected(this);
        new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        databaseReference = firebaseDatabase.getReference();
                        getContactData();
                        getInstalledAppsVersion();
                        getFirebaseData();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
    }

    public void recycleAdapter(){
        RecycleAdapter recycleAdapter = new RecycleAdapter(this,getParent());
        recycleView.setAdapter(recycleAdapter);
    }
    
    public void getInstalledAppsVersion(){

        for (int i = 0; i < 5; i++) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName[i], 0);
                installedVersion.add(i, String.valueOf(packageInfo.versionName));
            } catch (PackageManager.NameNotFoundException e) {
                installedVersion.add(i, null);
                e.printStackTrace();
            }
        }
    }

    public void getContactData(){

        databaseReference.child("contact").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    String key = snapshot1.getKey();
                    String value = (String) data.get(key);
                    contactDataList.add(value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
    public void getFirebaseData(){
        databaseReference.child("links").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    String key = snapshot1.getKey();
                    String value = (String) data.get(key);
                    apkUrls.add(value);
                }
                databaseReference.child("versions").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                            String key = snapshot1.getKey();
                            String value = (String) data.get(key);
                            latestVersion.add(value);
                        }
                        databaseReference.child("Readable Versions").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                                    String key = snapshot1.getKey();
                                    String value = (String) data.get(key);
                                    readableVersion.add(value);
                                }
                                dataReady = true;
                                recycleAdapter();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
    public void checkForUpdate(){
        if(updateDialogNotVisible){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    databaseReference.child("WA Update Manager").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                                String key = snapshot1.getKey();
                                String value = (String) data.get(key);
                                updateAppList.add(value);
                            }
                            try {
                                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
                                if(!packageInfo.versionName.equalsIgnoreCase(updateAppList.get(1))){
                                    updateDialog();
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }, 10000);
        }
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
                websiteIntent(MainActivity.this);
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
                downloadFile(MainActivity.this,updateAppList.get(0),getString(R.string.app_name),updateAppList.get(1));
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

    public void facebook(View view) {

        Intent facebookIntent = facebookIntent(this);
        startActivity(facebookIntent);
    }

    public void instagram(View view) {
        instagramIntent(this);
    }

    public void snapchat(View view) {
        snapchatIntent(this);
    }

    public void github(View view) {
        githubIntent(this);
    }

    public void website(View view) {
        websiteIntent(this);
    }
}