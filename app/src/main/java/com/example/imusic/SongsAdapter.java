package com.example.imusic;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {


    public static boolean isSelectMode=false;
    public static ArrayList<Integer> selectedSongs=new ArrayList<>();
    ArrayList<Song> localDataSet = new ArrayList<>();


    public SongsAdapter(ArrayList<Song> dataset) {

        localDataSet = dataset;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView songImage, songInfo;
        TextView songName;

        public ViewHolder(View view) {
            super(view);
            songImage = view.findViewById(R.id.songImage);
            songInfo = view.findViewById(R.id.songInfo);
            songName = view.findViewById(R.id.songName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.d("selecting multiple", "onClick:2  " + selectedSongs.toString());
                    if (isSelectMode) {
                        if (selectedSongs.contains(Integer.valueOf(localDataSet.get(getAdapterPosition()).getId()))) {
                            selectedSongs.remove(Integer.valueOf(localDataSet.get(getAdapterPosition()).getId()));
                            view.setBackgroundColor(Color.TRANSPARENT);
                        } else {

                            selectedSongs.add(Integer.valueOf(localDataSet.get(getAdapterPosition()).getId()));
                            view.setBackgroundColor(Color.argb(122, 122, 122, 122));
                        }

                        if (selectedSongs.size() == 0)
                            isSelectMode = false;
                        Log.d("selecting multiple", "onClick:3  " + selectedSongs.toString());
                        return;

                    }


                    Toast.makeText(songImage.getContext(), localDataSet.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
                    try {

                        MyPlayer.PlayMusic(localDataSet.get(getAdapterPosition()));

                        MainActivity.updateUI();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    isSelectMode = true;
                    Log.d("selecting multiple", "onLongClick:0  " + getAdapterPosition());
                    if (selectedSongs.contains(Integer.valueOf(localDataSet.get(getAdapterPosition()).getId()))) {
                        selectedSongs.remove(Integer.valueOf(localDataSet.get(getAdapterPosition()).getId()));
                        view.setBackgroundColor(Color.argb(222, 122, 122, 122));
                    } else {
                        selectedSongs.add(Integer.valueOf(localDataSet.get(getAdapterPosition()).getId()));
                        view.setBackgroundColor(Color.argb(122, 122, 122, 122));
                    }

                    if (selectedSongs.size() == 0)
                        isSelectMode = false;

                    Log.d("selecting multiple", "onClick:1  " + selectedSongs.toString());
                    return true;
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
