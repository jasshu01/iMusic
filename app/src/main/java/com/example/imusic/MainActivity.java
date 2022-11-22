package com.example.imusic;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    public static ImageView prev, play_pause, next;


    public static TextView currSong;

    public static ArrayList<Song> mySongs;
    public static MyPlayer myPlayer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prev = findViewById(R.id.previous);
        play_pause = findViewById(R.id.play_pause);
        next = findViewById(R.id.next);
        recyclerView = findViewById(R.id.SongListrecyclerView);
        currSong = findViewById(R.id.currSong);
        currSong.setText("Play Music");
        myPlayer = new MyPlayer(getApplicationContext());

        mySongs = myPlayer.getAll_MP3_Files();


        SongsAdapter songsAdapter = new SongsAdapter(mySongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(songsAdapter);

        play_pause.setImageResource(R.drawable.play_button);

        if (myPlayer.currSongPlaying.getName() != null) {
            currSong.setText(myPlayer.currSongPlaying.getName());
        }

        if (MyPlayer.mediaPlayer != null && MyPlayer.mediaPlayer.isPlaying()) {
            play_pause.setImageResource(R.drawable.pause);
        } else {
            play_pause.setImageResource(R.drawable.play_button);
        }


        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyPlayer.mediaPlayer.isPlaying()) {
                    play_pause.setImageResource(R.drawable.play_button);
                    MyPlayer.pauseMusic();
                } else {
                    if (myPlayer.currSongPlaying.getFile() != null) {

                        play_pause.setImageResource(R.drawable.pause);
                        try {
                            myPlayer.PlayMusic(myPlayer.currSongPlaying);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Select a Song", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    nextSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    prevSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        currSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MyPlayer.currSongPlaying.getName() != null) {
                    Intent intent = new Intent(MainActivity.this, CurrentView.class);
                    intent.putExtra("playingPosition", mySongs.indexOf(myPlayer.currSongPlaying));

                    MyActivityResultLauncher.launch(intent);

                }


            }
        });

    }

    ActivityResultLauncher<Intent> MyActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("onresult", "yess1 "+result.getResultCode());
                    if (result.getResultCode() == RESULT_OK) {
                        Log.d("onresult", "yess2");
                        if (MyPlayer.mediaPlayer != null && MyPlayer.mediaPlayer.isPlaying()) {
                            play_pause.setImageResource(R.drawable.pause);
                        } else {
                            play_pause.setImageResource(R.drawable.play_button);
                        }
                    }
                }
            });


    @Override
    public void onStart() {
        super.onStart();
        if (MyPlayer.currSongPlaying.getName() != null) {
            currSong.setText(myPlayer.currSongPlaying.getName());
        }

    }

    public void nextSong() throws IOException {
        int currIndex = mySongs.indexOf(myPlayer.currSongPlaying);
        Log.d("indexing", "curr " + currIndex);
        int newIndex = currIndex + 1;
        if (newIndex == mySongs.size()) {
            newIndex = 0;
        }

        MyPlayer.PlayMusic(mySongs.get(newIndex));
        currSong.setText(myPlayer.currSongPlaying.getName());
    }

    public void prevSong() throws IOException {
        int currIndex = mySongs.indexOf(MyPlayer.currSongPlaying);

        Log.d("indexing", "curr " + currIndex);
        int newIndex = currIndex - 1;
        if (newIndex == -1) {
            newIndex = mySongs.size() - 1;
        }
        MyPlayer.PlayMusic(mySongs.get(newIndex));
        currSong.setText(myPlayer.currSongPlaying.getName());
    }


}