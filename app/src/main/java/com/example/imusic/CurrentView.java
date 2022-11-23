package com.example.imusic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class CurrentView extends AppCompatActivity {

    TextView currentViewName;
    ImageView currentViewImage;
    ImageView currentViewPrev;
    ImageView currentViewPlayPause;
    ImageView currentViewNext;
    SeekBar currentViewSeekBar;
    MediaPlayer mediaPlayer;

    Thread updateSeekBar;

    ArrayList<Song> mySongs;

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

        mediaPlayer = MyPlayer.mediaPlayer;
        mySongs = MainActivity.mySongs;


        Intent intent = getIntent();
        int currentSongPosition = intent.getIntExtra("playingPosition", 0);


        Log.d("indexing", "recieving " + currentSongPosition);

        updateUI(MyPlayer.currSongPlaying);


        currentViewSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("currSong", MyPlayer.currSongPlaying.getName() + " " + MyPlayer.mediaPlayer.getCurrentPosition() + " " + seekBar.getMax());


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Log.d("currSong", MyPlayer.currSongPlaying.getName() + " " + MyPlayer.mediaPlayer.getCurrentPosition() + " " + seekBar.getMax());
                MyPlayer.mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        currentViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    nextSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        currentViewPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    prevSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        currentViewPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MyPlayer.mediaPlayer.isPlaying()) {
                    currentViewPlayPause.setImageResource(R.drawable.play_button);
                    MyPlayer.pauseMusic();
                } else {
                    if (MyPlayer.currSongPlaying.getFile() != null) {
                        currentViewPlayPause.setImageResource(R.drawable.pause);
                        try {
                            MyPlayer.PlayMusic(MyPlayer.currSongPlaying);
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
        currentViewSeekBar.setProgress(MyPlayer.mediaPlayer.getCurrentPosition());
        currentViewSeekBar.setMax(mediaPlayer.getDuration());

//        if (mediaPlayer.isPlaying())
        currentViewPlayPause.setImageResource(R.drawable.pause);
//        else
//            currentViewPlayPause.setImageResource(R.drawable.play_button);

//        updateSeekBar.start();

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                while (MyPlayer.mediaPlayer.getCurrentPosition() + 1000 < MyPlayer.mediaPlayer.getDuration()) {

                    currentViewSeekBar.setProgress(MyPlayer.mediaPlayer.getCurrentPosition());
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("currSong", "playing " + MyPlayer.mediaPlayer.isPlaying());

                }
                Log.d("currSong", "playing " + MyPlayer.mediaPlayer.isPlaying());

            }
        };

        updateSeekBar.start();


        MyPlayer.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    nextSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void nextSong() throws IOException {
        int currIndex = mySongs.indexOf(MyPlayer.currSongPlaying);
        Log.d("indexing", "curr " + currIndex);
        int newIndex = currIndex + 1;
        if (newIndex == mySongs.size()) {
            newIndex = 0;
        }


        MyPlayer.PlayMusic(mySongs.get(newIndex));
        updateUI(mySongs.get(newIndex));




    }

    public void prevSong() throws IOException {
        int currIndex = mySongs.indexOf(MyPlayer.currSongPlaying);
        Log.d("indexing", "curr " + currIndex);
        int newIndex = currIndex - 1;
        if (newIndex == -1) {
            newIndex = mySongs.size() - 1;
        }
        MyPlayer.PlayMusic(mySongs.get(newIndex));
        updateUI(mySongs.get(newIndex));
    }

    @Override
    public void onBackPressed() {

        Log.d("onresult", "backpressed");
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}