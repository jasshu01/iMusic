package com.example.imusic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView prev,play_pause,next;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prev=findViewById(R.id.previous);
        play_pause=findViewById(R.id.play_pause);
        next=findViewById(R.id.next);


        ArrayList<File> mySongs = new MyPlayer().getAll_MP3_Files();

        for (File file :
                mySongs) {
            Log.d("MySongName", file.getName());
        }



        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    new MyPlayer().PlayMusic(mySongs.get(0),MainActivity.this);

                } catch (IOException e) {
                    Log.d("playing",e.toString());
                    e.printStackTrace();
                }
            }
        });




    }
}