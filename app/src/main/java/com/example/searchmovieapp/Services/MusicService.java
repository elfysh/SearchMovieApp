package com.example.searchmovieapp.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.searchmovieapp.R;

public class MusicService extends Service {
    MediaPlayer ambientMediaPlayer;
    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate(){
        ambientMediaPlayer=MediaPlayer.create(this, R.raw.music);
        ambientMediaPlayer.setLooping(true);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        ambientMediaPlayer.start();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        ambientMediaPlayer.stop();
    }
}
