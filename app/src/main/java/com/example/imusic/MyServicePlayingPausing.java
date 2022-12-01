package com.example.imusic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;

public class MyServicePlayingPausing extends Service {
    public MyServicePlayingPausing() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (MyPlayer.mediaPlayer.isPlaying()) {
            MyPlayer.pauseMusic();

        } else {
            try {
                MyPlayer.PlayMusic(MyPlayer.currSongPlaying);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return super.onStartCommand(intent, flags, startId);
    }
}