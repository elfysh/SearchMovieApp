package com.example.searchmovieapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.searchmovieapp.R;
import com.example.searchmovieapp.Services.MusicService;
import com.example.searchmovieapp.databinding.ActivityIntroBinding;


public class IntroActivity extends AppCompatActivity {
ActivityIntroBinding binding;
    private static final String CHANNEL_ID = "my_channel_id";
    private static final String CHANNEL_NAME = "My Channel";
    private static final String CHANNEL_DESCRIPTION = "Description of my channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            }
        });
    }



    @Override
    protected void onPause() {
        stopService(new Intent(this, MusicService.class));
        super.onPause();
    }
}