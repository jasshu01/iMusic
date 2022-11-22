package com.example.imusic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyPlayer {
    ArrayList<Song> all_MP3_Files = new ArrayList<>();
    public static MediaPlayer mediaPlayer;
    Context context;
    TextView currSong;

    public MyPlayer(Context context) {
        this.context = context;
        this.currSong = MainActivity.currSong;

    }

    public ArrayList<Song> getAll_MP3_Files() {

        all_MP3_Files = new ArrayList<>();
        File file = Environment.getExternalStorageDirectory();
        fetchSongs(file);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            all_MP3_Files.sort(Song.songComparator);
        }
        return all_MP3_Files;
    }


    public void fetchSongs(File folder) {

        File[] myFiles = folder.listFiles();

        if (myFiles != null) {
            for (File file : myFiles) {


                if (!file.isHidden() && file.isDirectory()) {
                    fetchSongs(file);
                } else if (file.getName().endsWith(".mp3") && !file.getName().startsWith(".")) {

                    Song song = new Song();
                    song.setFile(file);

                    android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(file.getPath());

                    byte[] data = mmr.getEmbeddedPicture();
                    if (data != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        song.setImage(bitmap);
                    } else {
                        song.setImage(null);
                    }

                    String name = file.getName().split("[.]")[0];
                    if (name.length() > 30) {
                        name = name.substring(0, 27);
                        name += "...";
                    }

//                    Log.d("playing",name);
                    song.setName(name);
                    all_MP3_Files.add(song);
                }
            }
        }
    }

    public void PlayMusic(Song song, Context context) throws IOException {

        Uri uri = Uri.parse(song.getFile().toString());
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.start();

        currSong.setText(song.getName());


        Log.d("playingSong", song.getName() + " " + mediaPlayer);

    }

    public void pauseMusic() {


        Log.d("playingSong", "pausing " + mediaPlayer);

        if (mediaPlayer != null) {
            Log.d("playingSong", "pausing ");
            mediaPlayer.pause();
        }

    }


}
