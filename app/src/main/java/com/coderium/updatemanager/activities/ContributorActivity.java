package com.coderium.updatemanager.activities;

import static com.coderium.updatemanager.utilities.Constant.waUpdateManagerList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.coderium.updatemanager.R;
import com.coderium.updatemanager.adapters.ContributorAdapter;
import com.coderium.updatemanager.extraClasses.Contributor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ContributorActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView recycleView;

    Contributor contributor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contributor);

        // setting up recycle view

        recycleView = findViewById(R.id.recycleViewDonators);

        // creating database instance

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Calling Function that fetch data from firebase
        contributor = new Contributor();
        getContributorsDataFromFirebase("Contributors/");

    }


    public void recycleAdapter(){
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        ContributorAdapter contributorAdapter = new ContributorAdapter(this,contributor);
        recycleView.setAdapter(contributorAdapter);
    }

    public void getContributorsDataFromFirebase(String path){

        databaseReference.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                contributor.nameList.clear();
                contributor.socialLink.clear();

                Map<String, Object> data = (Map<String, Object>) snapshot.getValue();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    String key = snapshot1.getKey();
                    contributor.nameList.add(key);
                    contributor.socialLink.add(data.get(key).toString());

                }
                recycleAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void backButton(View view) {
        onBackPressed();
    }

    public void joinCoderium(View view) {

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(waUpdateManagerList.get(6)));
            startActivity(browserIntent);
        }catch (Exception ex){
            Toast.makeText(ContributorActivity.this, getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();
        }

    }
}