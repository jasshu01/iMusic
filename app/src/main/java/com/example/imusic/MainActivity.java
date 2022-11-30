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
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView showOptions;
    public static ImageView prev, play_pause, next;

    public static TextView currSong;
    public static ArrayList<Song> mySongs;
    public static MyPlayer myPlayer;
    static Thread updateSeekBar;
    static SeekBar seekBar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prev = findViewById(R.id.previous);
        play_pause = findViewById(R.id.play_pause);
        next = findViewById(R.id.next);
        recyclerView = findViewById(R.id.SongListrecyclerView);
        seekBar = findViewById(R.id.seekBar2);
        currSong = findViewById(R.id.currSong);
        showOptions = findViewById(R.id.showOptions);


        currSong.setText("Play Music");
        myPlayer = new MyPlayer(getApplicationContext());



        mySongs = myPlayer.getAll_MP3_Files();

        for (int i = 0; i < mySongs.size(); i++) {
            Log.d("mysong", mySongs.get(i).getName() + " " + mySongs.get(i).getId());
        }


        Log.d("mysong", "starting " + mySongs.size());
        SongsAdapter songsAdapter = new SongsAdapter(mySongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(songsAdapter);

        play_pause.setImageResource(R.drawable.play_button);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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


        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyPlayer.mediaPlayer != null && MyPlayer.mediaPlayer.isPlaying()) {
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


        showOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.actions, popup.getMenu());
                popup.show();

                Menu items = popup.getMenu();
                if (SongsAdapter.isSelectMode) {

                    MenuItem item = items.findItem(R.id.playlists);
                    item.setEnabled(false);

                    item = items.findItem(R.id.createPlaylist);
                    if (SongsAdapter.selectedSongs.size() > 1) {
                        item.setEnabled(true);
                    } else {
                        item.setEnabled(false);
                    }

                } else {

                    MenuItem item = items.findItem(R.id.createPlaylist);
                    item.setEnabled(false);
                }


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.createPlaylist: {


                                SongsAdapter.isSelectMode = false;


                                ArrayList<Integer> arr = SongsAdapter.selectedSongs;


                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Set Playlist Name");

                                final EditText input = new EditText(MainActivity.this);

                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                builder.setView(input);

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String PlaylistName = input.getText().toString();
                                        MyPlayer.handler.addPlaylist(new Playlist(PlaylistName, arr));
                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                    }
                                });

                                builder.show();

                            }

                        }


                        return false;
                    }
                });

            }
        });


    }

    ActivityResultLauncher<Intent> MyActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("onresult", "yess1 " + result.getResultCode());
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
        updateUI();
    }

    public static void nextSong() throws IOException {
        int currIndex = mySongs.indexOf(myPlayer.currSongPlaying);
        Log.d("indexing", "curr " + currIndex);
        int newIndex = currIndex + 1;
        if (newIndex == mySongs.size()) {
            newIndex = 0;
        }

        MyPlayer.PlayMusic(mySongs.get(newIndex));
        currSong.setText(myPlayer.currSongPlaying.getName());
        updateUI();
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
        updateUI();
    }


    public static void updateUI() {
//        currentViewImage.setImageBitmap(song.getImage());

        if (myPlayer.currSongPlaying.getName() != null) {
            currSong.setText(myPlayer.currSongPlaying.getName());
        } else {
            currSong.setText("Play Music");
//            seekBar.setVisibility(View.GONE);
            return;
        }

//        seekBar.setVisibility(View.VISIBLE);

        currSong.setText(myPlayer.currSongPlaying.getName());
        seekBar.setProgress(MyPlayer.mediaPlayer.getCurrentPosition());
        seekBar.setMax(MyPlayer.mediaPlayer.getDuration());


        if (MyPlayer.mediaPlayer != null && MyPlayer.mediaPlayer.isPlaying()) {
            play_pause.setImageResource(R.drawable.pause);
        } else {
            play_pause.setImageResource(R.drawable.play_button);
        }


        updateSeekBar = new Thread() {
            @Override
            public void run() {
                while (MyPlayer.mediaPlayer.getCurrentPosition() + 1000 < MyPlayer.mediaPlayer.getDuration()) {

                    seekBar.setProgress(MyPlayer.mediaPlayer.getCurrentPosition());
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


}