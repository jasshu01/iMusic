package com.example.imusic;

import java.util.ArrayList;

public class Playlist {
    String name;
    int id;
    ArrayList<Integer> songIDs;

    public Playlist() {
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", songIDs=" + songIDs +
                '}';
    }

    public Playlist(String name, ArrayList<Integer> songIDs) {
        this.name = name;
        this.songIDs = songIDs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Integer> getSongIDs() {
        return songIDs;
    }

    public void setSongIDs(ArrayList<Integer> songs) {
        this.songIDs = songs;
    }
}

