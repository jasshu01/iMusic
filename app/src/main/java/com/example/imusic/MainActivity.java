package com.example.imusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
    Boolean playing = true;
    MediaPlayer mediaPlayer;
    public static TextView currSong;


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
        MyPlayer myPlayer = new MyPlayer(getApplicationContext());


        ArrayList<Song> mySongs = myPlayer.getAll_MP3_Files();


        SongsAdapter songsAdapter = new SongsAdapter(mySongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(songsAdapter);

        play_pause.setImageResource(R.drawable.play_button);

        if (myPlayer.currSongPlaying.getName() != null) {
            currSong.setText(myPlayer.currSongPlaying.getName());
        }

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPlayer.isPlaying) {
                    play_pause.setImageResource(R.drawable.play_button);
                    myPlayer.pauseMusic();
                } else {
                    play_pause.setImageResource(R.drawable.pause);
                    if (myPlayer.currSongPlaying.getFile() != null) {
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
                    myPlayer.nextSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    myPlayer.prevSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        currSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,CurrentView.class);
                intent.putExtra("playingPosition",mySongs.indexOf(myPlayer.currSongPlaying));
                startActivity(intent);
            }
        });

    }
}