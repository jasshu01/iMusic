package com.example.imusic;

import static java.lang.Thread.sleep;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView showOptions;
    public static ImageView prev, play_pause, next;
    public static TextView currSong;
    public static ArrayList<Playlist> myPlaylists;
    public static MyPlayer myPlayer;
    static Thread updateSeekBar;
    static SeekBar seekBar;
    public static int frame;
    TextView viewHomePage, viewPlaylists;


    public static int HOMEPAGE_CODE = 2001;
    public static int PLAYLIST_PAGE_CODE = 2002;
    public static int CurrentFragment = HOMEPAGE_CODE;

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


        new Thread() {
            int i = 0;

            public void run() {
                while (i < 10) {


                    i++;
                    int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("running", " " + finalI);
                            currSong.setText("currently Playing" + " " + finalI);
                        }

                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }

        }.start();


        currSong.setText("Play Music");
        myPlayer = new MyPlayer(getApplicationContext());


        myPlaylists = myPlayer.handler.allPlaylists();

        FragmentManager fm = getSupportFragmentManager();
        frame = R.id.HomepageFrame;
        FragmentTransaction ft = fm.beginTransaction();


        ft.replace(frame, new HomepageFragment());
        ft.commit();


        viewHomePage = findViewById(R.id.viewHomePage);
        viewPlaylists = findViewById(R.id.viewPlaylists);


        viewHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentFragment = HOMEPAGE_CODE;
                FragmentTransaction ft = fm.beginTransaction();

                ft.replace(frame, new HomepageFragment());
                ft.commit();
            }
        });

        viewPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentFragment = PLAYLIST_PAGE_CODE;
                FragmentTransaction ft = fm.beginTransaction();

                ft.replace(frame, new PlaylistsFragment());
                ft.commit();
            }
        });


        play_pause.setImageResource(R.drawable.play_button);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    intent.putExtra("playingPosition", MyPlayer.findCurrSongPosition());

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
            currSong.setText(MyPlayer.currSongPlaying.getName());
        }
        updateUI();
    }

    public static void nextSong() throws IOException {
        if (MyPlayer.currSongPlaying.getName() == null)
            return;
//
//        Log.d("indexing1", MyPlayer.currSongPlaying.toString());
//
//        for (Song song :
//                MyPlayer.currPlayingPlaylist) {
//            Log.d("indexing1", song.toString());
//        }
//
//        int currIndex = MyPlayer.currPlayingPlaylist.indexOf(MyPlayer.currSongPlaying);

        int currIndex = MyPlayer.findCurrSongPosition();
        Log.d("indexing1", "curr " + currIndex);
        int newIndex = currIndex + 1;
        if (newIndex == MyPlayer.currPlayingPlaylist.size()) {
            newIndex = 0;
        }

        MyPlayer.PlayMusic(MyPlayer.currPlayingPlaylist.get(newIndex));
        currSong.setText(MyPlayer.currSongPlaying.getName());
        updateUI();
    }

    public void prevSong() throws IOException {

        if (MyPlayer.currSongPlaying.getName() == null)
            return;
        int currIndex = MyPlayer.findCurrSongPosition();

        Log.d("indexing", "curr " + currIndex);
        int newIndex = currIndex - 1;
        if (newIndex == -1) {
            newIndex = MyPlayer.currPlayingPlaylist.size() - 1;
        }
        MyPlayer.PlayMusic(MyPlayer.currPlayingPlaylist.get(newIndex));
        currSong.setText(MyPlayer.currSongPlaying.getName());
        updateUI();
    }


    public static void updateUI() {

        if (myPlayer.currSongPlaying.getName() != null) {
            currSong.setText(myPlayer.currSongPlaying.getName());
        } else {
            currSong.setText("Play Music");
            return;
        }

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


                }


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