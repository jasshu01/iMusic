package com.example.imusic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class CurrentView extends AppCompatActivity {

    static TextView currentViewName;
    static ImageView currentViewImage;
    ImageView currentViewPrev;
    ImageView currentViewPlayPause;
    ImageView currentViewNext;
    static SeekBar currentViewSeekBar;
    MyPlayer myPlayer;
    Song currSongPlaying;
    Thread updateSeekBar;

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

        currentViewSeekBar.setMax(MyPlayer.mediaPlayer.getDuration());
        currentViewSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MyPlayer.mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        currentViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    myPlayer.nextSong();
                    currSongPlaying = myPlayer.currSongPlaying;
                    updateUI(currSongPlaying);

                    Log.d("noww", currSongPlaying.getName());
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


        updateSeekBar = new Thread() {
            @Override
            public void run() {
                while (MyPlayer.mediaPlayer.getCurrentPosition() < MyPlayer.mediaPlayer.getDuration()) {

                    currentViewSeekBar.setProgress(MyPlayer.mediaPlayer.getCurrentPosition());
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    myPlayer.nextSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        };

        updateSeekBar.start();
    }

    public static void updateUI(Song song) {
        currentViewImage.setImageBitmap(song.getImage());
        currentViewName.setText(song.getName());
        currentViewSeekBar.setProgress(0);
    }
}