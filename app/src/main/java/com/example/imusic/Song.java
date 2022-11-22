package com.example.imusic;

import android.graphics.Bitmap;

import java.io.File;
import java.util.Comparator;

public class Song {
    Bitmap image;
    File file;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Song() {
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    public static Comparator<Song> songComparator = new Comparator<Song>() {


        public int compare(Song s1, Song s2) {
            String name1 = s1.getName();
            String name2 = s2.getName();

            return name1.compareTo(name2);

        }
    };
}
