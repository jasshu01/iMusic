package com.example.imusic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MyServicePlayingNext extends Service {
    public MyServicePlayingNext() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        try {
            MyPlayer.playNextSong();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("myservicenoti", "yess");

        return START_STICKY;
    }
}