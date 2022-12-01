package com.example.imusic;

import static com.example.imusic.MainActivity.frame;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;


public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.ViewHolder> {


    ArrayList<Playlist> localDataSet = new ArrayList<>();


    public PlaylistsAdapter(ArrayList<Playlist> dataset) {

        localDataSet = dataset;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView songImage, playListOptions;
        TextView playlistName;

        public ViewHolder(View view) {
            super(view);
//            songImage = view.findViewById(R.id.songImage);
//            songInfo = view.findViewById(R.id.songInfo);
            playlistName = view.findViewById(R.id.playlistName);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ArrayList<Song> playlistSongs = new ArrayList<>();

                    for (Integer i :
                            localDataSet.get(getAdapterPosition()).getSongIDs()) {
                        playlistSongs.add(MyPlayer.handler.getSongFromId(i));
                    }

                    MyPlayer.currPlayingPlaylist = playlistSongs;
                    Log.d("indexing1", "updating playlist");


                    Toast.makeText(view.getContext(), localDataSet.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();

                    FragmentTransaction ft = ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(frame, new PlaylistSongsFragment(localDataSet.get(getAdapterPosition())));
                    ft.commit();


                }
            });


        }


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.playlist_view, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d("myposition", String.valueOf(position));
        Log.d("playing", localDataSet.get(position).getName());

        holder.playlistName.setText(localDataSet.get(position).getName());
//        holder.songInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(), localDataSet.get(position).getName() + " " + position, Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
