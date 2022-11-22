package com.example.imusic;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {


    ArrayList<Song> localDataSet = new ArrayList<>();

    MyPlayer myPlayer;

    public SongsAdapter(ArrayList<Song> dataset) {

        localDataSet = dataset;
//        localDataSet = myPlayer.getAll_MP3_Files();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView songImage, songInfo;
        TextView songName;

        public ViewHolder(View view) {
            super(view);
            songImage = view.findViewById(R.id.songImage);
            songInfo = view.findViewById(R.id.songInfo);
            songName = view.findViewById(R.id.songName);
            myPlayer = new MyPlayer(songImage.getContext());


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(songImage.getContext(), localDataSet.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
                    try {
                        myPlayer.PlayMusic(localDataSet.get(getAdapterPosition()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.song_view, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d("myposition", String.valueOf(position));
        holder.songImage.setImageBitmap(localDataSet.get(position).getImage());
        Log.d("playing", localDataSet.get(position).getName());

        holder.songName.setText(localDataSet.get(position).getName());

        holder.songInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), localDataSet.get(position).getName() + " " + position, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
