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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyPlayer {
    static ArrayList<Song> all_MP3_Files = new ArrayList<>();
    public static MediaPlayer mediaPlayer;
    static Context context;

    static int songPosition = 0;

    public static Song currSongPlaying = new Song();


    public MyPlayer(Context context) {
        this.context = context;
    }


    public static ArrayList<Song> getAll_MP3_Files() {

        all_MP3_Files = new ArrayList<>();
        File file = Environment.getExternalStorageDirectory();
        fetchSongs(file);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            all_MP3_Files.sort(Song.songComparator);
        }
        return all_MP3_Files;
    }


    public static void fetchSongs(File folder) {

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

    public static void PlayMusic(Song song) throws IOException {

        Log.d("playingSong", song.getName() + " " + mediaPlayer);
        Log.d("playingSong", currSongPlaying.getName() + " " + mediaPlayer);
        if (mediaPlayer != null) {
            if (song == currSongPlaying) {
                mediaPlayer.start();
                mediaPlayer.seekTo(songPosition);
                return;
            }
        }

        Uri uri = Uri.parse(song.getFile().toString());
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        songPosition = 0;

        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.start();


        currSongPlaying = song;


        Log.d("playingSong", song.getName() + " " + mediaPlayer);

    }

    public static void pauseMusic() {

        Log.d("playingSong", "pausing " + mediaPlayer);
        if (mediaPlayer != null) {
            Log.d("playingSong", "pausing ");
            mediaPlayer.pause();
            songPosition = mediaPlayer.getCurrentPosition();

        }
    }


}
