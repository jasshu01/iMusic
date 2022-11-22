package com.example.imusic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class CurrentView extends AppCompatActivity {

    TextView currentViewName;
    ImageView currentViewImage, currentViewPrev, currentViewPlayPause, currentViewNext;
    SeekBar currentViewSeekBar;
    MyPlayer myPlayer;
    Song currSongPlaying;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_view);
        currentViewImage = findViewById(R.id.currentViewImage);
        currentViewPrev = findViewById(R.id.currentViewPrev);
        currentViewPlayPause = findViewById(R.id.currentViewPlayPause);
        currentViewNext = findViewById(R.id.currentViewNext);
        currentViewName = findViewById(R.id.currentViewName);
        currentViewSeekBar = findViewById(R.id.currentViewSeekBar);

        Intent intent = getIntent();
        int currentSongPosition = intent.getIntExtra("playingPosition", 0);
        myPlayer = new MyPlayer(getApplicationContext());

        currSongPlaying = myPlayer.getAll_MP3_Files().get(currentSongPosition);

        currentViewImage.setImageBitmap(currSongPlaying.getImage());
        currentViewName.setText(currSongPlaying.getName());

        currentViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    myPlayer.nextSong();
                    currSongPlaying = myPlayer.currSongPlaying;
                    updateUI(currSongPlaying);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        currentViewPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    myPlayer.prevSong();

                    currSongPlaying = myPlayer.currSongPlaying;
                    updateUI(currSongPlaying);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        currentViewPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPlayer.isPlaying) {
                    currentViewPlayPause.setImageResource(R.drawable.play_button);
                    myPlayer.pauseMusic();
                } else {
                    currentViewPlayPause.setImageResource(R.drawable.pause);
                    if (myPlayer.currSongPlaying.getFile() != null) {
                        try {
                            myPlayer.PlayMusic(myPlayer.currSongPlaying);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


    }

    public void updateUI(Song song) {
        currentViewImage.setImageBitmap(song.getImage());
        currentViewName.setText(song.getName());
    }
}