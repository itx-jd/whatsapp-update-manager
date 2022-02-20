package com.codex.updatemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static com.codex.updatemanager.Constant.internetAvailable;
import static com.codex.updatemanager.Constant.internetIsConnected;
import static com.codex.updatemanager.Constant.isConnected;

public class SplashActivity extends AppCompatActivity {

    ImageView logoImage;
    TextView logoText,codeXText;
    Animation topAnim,botAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        logoImage = findViewById(R.id.logoImage);
        logoText = findViewById(R.id.logoText);
        codeXText = findViewById(R.id.codeXText);
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        botAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        logoImage.setAnimation(topAnim);
        logoText.setAnimation(topAnim);
        codeXText.setAnimation(botAnim);

        internetAvailable = isConnected(this);

        new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                }, 3000);


    }
}