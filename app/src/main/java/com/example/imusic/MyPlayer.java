package com.example.imusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyPlayer {
    private static final String NOTIFICATION_CONTROLLER_CHANNEL_ID = "101";
    private static final int NOTIFICATION_ID = 101;
    static ArrayList<Song> all_MP3_Files = new ArrayList<>();
    public static MediaPlayer mediaPlayer;
    static Context context;

    static int songPosition = 0;

    public static Song currSongPlaying = new Song();
    public static dbHandler handler;

    public static ArrayList<Song> currPlayingPlaylist = new ArrayList<>();

    public MyPlayer(Context context) {
        this.context = context;
        handler = new dbHandler(context, "iMusic", null, 1);
        getAll_MP3_Files();
        handler.allPlaylists();
    }

    public static void playNextSong() throws IOException {

        int currIndex = findCurrSongPosition();
        Log.d("indexing1", "curr " + currIndex);
        int newIndex = currIndex + 1;
        if (newIndex == currPlayingPlaylist.size()) {
            newIndex = 0;
        }

        PlayMusic(MyPlayer.currPlayingPlaylist.get(newIndex));
    }

    public static void playPrevSong() throws IOException {

        int currIndex = findCurrSongPosition();
        Log.d("indexing1", "curr " + currIndex);
        int newIndex = currIndex - 1;
        if (newIndex == -1) {
            newIndex = MyPlayer.currPlayingPlaylist.size() - 1;
        }

        PlayMusic(MyPlayer.currPlayingPlaylist.get(newIndex));
    }

    public ArrayList<Song> getAll_MP3_Files() {

        all_MP3_Files = new ArrayList<>();
        File file = Environment.getExternalStorageDirectory();
        fetchSongs(file);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            all_MP3_Files.sort(Song.songComparator);
        }

        for (int i = 0; i < all_MP3_Files.size(); i++) {

            Log.d("playing", all_MP3_Files.get(i).getName());
            if (!handler.songExists(all_MP3_Files.get(i))) {
                long k = handler.addSong(all_MP3_Files.get(i));
                all_MP3_Files.get(i).setId((int) k);

            } else {
                int k = handler.getSongId(all_MP3_Files.get(i));
                all_MP3_Files.get(i).setId(k);
                Log.d("fetchingSong", "already exists");
            }
        }

        currPlayingPlaylist = all_MP3_Files;
        return all_MP3_Files;
    }


    public static Song getSongFromFilePath(String filepath) {
        Song song = new Song();

        File file = new File(filepath);

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

        Log.d("SongFetch", name);
        song.setName(name);

        return song;
    }


    public static void fetchSongs(File folder) {

        File[] myFiles = folder.listFiles();

        if (myFiles != null) {
            for (File file : myFiles) {


                if (!file.isHidden() && file.isDirectory()) {
                    fetchSongs(file);
                } else if (file.getName().endsWith(".mp3") && !file.getName().startsWith(".")) {

                    Song song = getSongFromFilePath(file.getAbsolutePath());
                    all_MP3_Files.add(song);
                }
            }
        }
    }

    public static void PlayMusic(Song song) throws IOException {

        Log.d("playingSong", song.getName() + " song " + mediaPlayer);
        Log.d("playingSong", currSongPlaying.getName() + " curr " + mediaPlayer);
        if (mediaPlayer != null) {
            if (song == currSongPlaying) {
                mediaPlayer.start();
                mediaPlayer.seekTo(songPosition);
                createNotification(true);

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


        createNotification(true);

        Intent intent = new Intent();
        intent.setAction("com.jasshugarg.imusic.currsong");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("currPlayingSong", currSongPlaying.getName());


//        intent.setComponent(new ComponentName("com.example.mysongreceiverapp1", "com.example.mysongreceiverapp1.MyMusicReceiver1"));
//        context.sendOrderedBroadcast(intent, null);
//        intent.setComponent(new ComponentName("com.example.mysongreceiverapp2", "com.example.mysongreceiverapp2.MyMusicReceiver2"));


        Log.d("MusicBroadcast", "sending " + currSongPlaying.getName());
        context.sendOrderedBroadcast(intent, null);
//        context.sendOrderedBroadcast(intent, "com.jasshugarg.musicinfo");
//        context.sendBroadcast(intent);
        Log.d("playingSong", currSongPlaying.getName() + " curr2 " + mediaPlayer);

    }

    public static void createNotification(boolean playing) {

        Log.d("currently"," "+playing);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CONTROLLER_CHANNEL_ID, "iMusic Notification Channel", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        PendingIntent nextSongFromNotification = PendingIntent.getService(context, 0, new Intent(context, MyServicePlayingNext.class), PendingIntent.FLAG_MUTABLE);
        PendingIntent prevSongFromNotification = PendingIntent.getService(context, 0, new Intent(context, MyServicePlayingPrevious.class), PendingIntent.FLAG_MUTABLE);
        PendingIntent controllerFromNotification = PendingIntent.getService(context, 0, new Intent(context, MyServicePlayingPausing.class), PendingIntent.FLAG_MUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CONTROLLER_CHANNEL_ID);
        builder.setContentTitle(currSongPlaying.getName());
        builder.setSmallIcon(R.drawable.play_button);
        builder.setLargeIcon(currSongPlaying.getImage());
        builder.setStyle(new NotificationCompat.BigPictureStyle());
        builder.addAction(R.drawable.previous, "previous", prevSongFromNotification);
        if (playing)
            builder.addAction(R.drawable.pause, "play_pause", controllerFromNotification);
        else
            builder.addAction(R.drawable.play_button, "play_pause", controllerFromNotification);

        builder.addAction(R.drawable.next, "next", nextSongFromNotification);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle());
        builder.setAutoCancel(false);
        builder.setOnlyAlertOnce(true);
        builder.setOngoing(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(NOTIFICATION_ID, builder.build());

    }

    public static void pauseMusic() {

        Log.d("playingSong", "pausing " + mediaPlayer);
        if (mediaPlayer != null) {
            Log.d("playingSong", "pausing ");
            mediaPlayer.pause();
            songPosition = mediaPlayer.getCurrentPosition();

            createNotification(false);
        }
    }

    public static int findCurrSongPosition() {
        int pos = -1;

        for (int i = 0; i < currPlayingPlaylist.size(); i++) {
            if (currPlayingPlaylist.get(i).getId() == currSongPlaying.getId())
                return i;
        }
        return pos;
    }
}
