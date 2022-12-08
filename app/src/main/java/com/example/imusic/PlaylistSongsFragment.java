package com.example.imusic;

//import static com.example.imusic.MainActivity.mySongs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;


public class PlaylistSongsFragment extends Fragment {

    ImageView playlistSongImage;
    FloatingActionButton playlistSongPlayButton;
    TextView playlistSongsName;
    RecyclerView playlistSongsRecyclerView;

    Playlist playlist;

    public PlaylistSongsFragment() {

    }

    public PlaylistSongsFragment(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist_songs, container, false);
        playlistSongImage = view.findViewById(R.id.playlistSongImage);
        playlistSongPlayButton = view.findViewById(R.id.playlistSongPlayButton);
        playlistSongsName = view.findViewById(R.id.playlistSongsName);
        playlistSongsRecyclerView = view.findViewById(R.id.playlistSongsRecyclerView);
        playlistSongsName.setText(playlist.getName());
        ArrayList<Song> playlistSongs = new ArrayList<>();

        for (Integer i :
                playlist.getSongIDs()) {
            playlistSongs.add(MyPlayer.handler.getSongFromId(i));
        }

        SongsAdapter songsAdapter = new SongsAdapter(playlistSongs);
        playlistSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistSongsRecyclerView.setAdapter(songsAdapter);


        playlistSongPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MyPlayer.currPlayingPlaylist = playlistSongs;
                    MyPlayer.PlayMusic(MyPlayer.currPlayingPlaylist.get(0));
                    MainActivity.updateUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        return view;

    }
}