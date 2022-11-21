package com.example.imusic;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyPlayer {
    ArrayList<File> all_MP3_Files = new ArrayList<>();
    MediaPlayer mediaPlayer;


    public ArrayList<File> getAll_MP3_Files() {

        all_MP3_Files = new ArrayList<>();
        File file = Environment.getExternalStorageDirectory();
        fetchSongs(file);

        return all_MP3_Files;
    }


    public void fetchSongs(File folder) {

        File[] myFiles = folder.listFiles();

        if (myFiles != null) {
            for (File file : myFiles) {

                if (!file.isHidden() && file.isDirectory()) {
                    fetchSongs(file);
                } else if (file.getName().endsWith(".mp3") && !file.getName().startsWith(".")) {
                    all_MP3_Files.add(file);
                }
            }
        }
    }

    public void PlayMusic(File file, Context context) throws IOException {

        Uri uri = Uri.parse(file.toString());
        mediaPlayer = MediaPlayer.create(context, uri);
//        mediaPlayer.setDataSource(file.getPath());

        mediaPlayer.start();

        Log.d("playing", file.getName());

        while (mediaPlayer.isPlaying()) {
            Log.d("playing", String.valueOf(mediaPlayer.getCurrentPosition()));
        }


    }


}
