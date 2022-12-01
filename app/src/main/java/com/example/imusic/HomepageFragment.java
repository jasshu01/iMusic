package com.example.imusic;

//import static com.example.imusic.MainActivity.mySongs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomepageFragment extends Fragment {

    RecyclerView recyclerView;

    public HomepageFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        recyclerView = view.findViewById(R.id.SongListrecyclerView);
//        MyPlayer.currPlayingPlaylist = MyPlayer.all_MP3_Files;
        SongsAdapter songsAdapter = new SongsAdapter(MyPlayer.all_MP3_Files);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(songsAdapter);

        return view;

    }
}