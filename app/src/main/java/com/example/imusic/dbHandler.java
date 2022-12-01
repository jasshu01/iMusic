package com.example.imusic;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class dbHandler extends SQLiteOpenHelper {

    public long sNo = 1;
    Context context;
    boolean cancelSaving = false;
    boolean addinExisting = false;
    boolean sameName = false;
    Dialog dialog, dialog1, dialog2;


    public dbHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE SONGS (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,NAME TEXT,FILE TEXT)";
        String create2 = "CREATE TABLE PLAYLIST (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,PLAYLIST_NAME TEXT,PLAYLIST_SONGS TEXT)";
        db.execSQL(create);
        db.execSQL(create2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    @SuppressLint("SuspiciousIndentation")
    public long addSong(Song song) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put("name", song.getName());
        contentValues.put("file", song.getFile().getAbsolutePath().toString());

        long k = db.insert("Songs", null, contentValues);

        Log.d("addingSong", " " + k);

        return k;
    }

    public boolean songExists(Song song) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM SONGS where FILE=\"" + song.getFile().getAbsolutePath().toString() + "\"";
        Cursor cursor = db.rawQuery(query, null);

        Log.d("fetchingSong", "fetchSong: " + query);

        if (cursor != null && cursor.moveToFirst()) {
            Log.d("fetchingSong", "fetchSong: " + cursor);
            return true;
        } else {
            Log.d("fetchingSong", "fetchSong: " + "nothing");
            return false;
        }


    }


    public ArrayList<Playlist> allPlaylists() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "Select *  from PLAYLIST";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Playlist playlist = new Playlist();
                Log.d("allSongs", "allSongs: " + cursor.getString(1));
                playlist.setId(Integer.parseInt(cursor.getString(0)));
                playlist.setName(cursor.getString(1));

                String ids = cursor.getString(2);
                ids = ids.substring(1, ids.length() - 1);
                ArrayList<Integer> songIDs = new ArrayList<>();
                String[] arr = ids.split(", ");


                Log.d("fetchingPlaylists", "allPlaylists: " + ids);
                Log.d("fetchingPlaylists", "allPlaylists: " + arr.toString());

                for (String str :
                        arr) {
                    songIDs.add(Integer.parseInt(str));
                }

                playlist.setSongIDs(songIDs);


                playlists.add(playlist);
                Log.d("fetchingPlaylists", "allPlaylists: " + playlist.toString());
            } while (cursor.moveToNext());
        }

        return playlists;
    }
//
//    public Song fetchSong(int Sno) {
//        Song song = new Song();
//        SQLiteDatabase db = getReadableDatabase();
//        String query = "Select * from Songs where sno=" + Sno + ";";
//        Cursor cursor = db.rawQuery(query, null);
//
//        Log.d("fetchingSong", "fetchSong: " + query);
//
//        if (cursor != null) {
//            cursor.moveToFirst();
//
//            song.setSno(Integer.parseInt(cursor.getString(0)));
//            song.setFirstName(cursor.getString(1));
//            song.setLastName(cursor.getString(2));
//            song.setPhone1(cursor.getString(3));
//            song.setPhone2(cursor.getString(4));
//            song.setEmail(cursor.getString(5));
//            song.setFav(cursor.getInt(6));
//        }
//        else{
//            return null;
//        }
//
//
//
//        return song;
//    }
//
//    public SongGroup fetchGroup(int Sno) {
//        SongGroup playlist = new SongGroup();
//        SQLiteDatabase db = getReadableDatabase();
//        String query = "Select * from PLAYLIST where sno=" + Sno + ";";
//        Cursor cursor = db.rawQuery(query, null);
//
//        Log.d("SongGroup", "SongGroup: " + query);
//
//        if (cursor != null) {
//            cursor.moveToFirst();
//
//            playlist.setSno(Integer.parseInt(cursor.getString(0)));
//            playlist.setGroupName(cursor.getString(1));
//            playlist.setGroupMembers(cursor.getString(2));
//        }
//        else{
//            return null;
//        }
//
//        Log.d("fetchinggroup", "fetchGroup: "+playlist);
//        return playlist;
//    }
//


    public Boolean addPlaylist(Playlist playlist) {


        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("PLAYLIST_NAME", playlist.getName());

        ArrayList<Integer> playlistSongsIndexes = playlist.getSongIDs();

        Collections.sort(playlistSongsIndexes);

        contentValues.put("PLAYLIST_SONGS", playlistSongsIndexes.toString());


        long k = db.insert("PLAYLIST", null, contentValues);
        db.close();
        Log.d("addPlaylist", "addPlaylist: " + "playlist " + playlist.toString());
        Log.d("addPlaylist", "addPlaylist: " + k);

        return true;
    }

    public int getSongId(Song song) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM SONGS where FILE=\"" + song.getFile().getAbsolutePath().toString() + "\"";
        Cursor cursor = db.rawQuery(query, null);

        Log.d("fetchingSong", "fetchSong: " + query);

        if (cursor != null && cursor.moveToFirst()) {

            Log.d("fetchingSong", "fetchSong: " + cursor.getString(0));
            Log.d("fetchingSong", "fetchSong: " + cursor.getString(1));
            Log.d("fetchingSong", "fetchSong: " + cursor.getString(2));

            Log.d("fetchingSong", "fetchSong: " + cursor);
            return Integer.parseInt(cursor.getString(0));
        } else {

            Log.d("fetchingSong", "fetchSong: " + "nothing");
            return 0;
        }

    }

    public Song getSongFromId(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM SONGS where id=" + id + ";";
        Cursor cursor = db.rawQuery(query, null);

        Log.d("fetchingSong", "fetchSong: " + query);

        if (cursor != null && cursor.moveToFirst()) {
            Song song = new Song();

            song = MyPlayer.getSongFromFilePath(cursor.getString(2));
            song.setId(Integer.parseInt(cursor.getString(0)));

            return song;
        } else {

            return null;
        }

    }


//    public void updateGroup(SongGroup playlist) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put("Group_Name", playlist.getGroupName());
//        contentValues.put("Group_Members", playlist.getGroupMembers());
//
//
//        db.update("PLAYLIST", contentValues, "sno=?", new String[]{String.valueOf(playlist.getSno())});
//
//
//    }


//    public ArrayList<SongGroup> fetchGroups() {
//        ArrayList<SongGroup> groupsArr = new ArrayList<>();
//        SQLiteDatabase db = getReadableDatabase();
//        String query = "Select *  from PLAYLIST";
//        Cursor cursor = db.rawQuery(query, null);
//
//        if (cursor!=null && cursor.moveToFirst()) {
//            do {
//                SongGroup playlist = new SongGroup();
//                Log.d("allSongs", "groupname: " + cursor.getString(1));
//                playlist.setSno(Integer.parseInt(cursor.getString(0)));
//                playlist.setGroupName(cursor.getString(1));
//                playlist.setGroupMembers(cursor.getString(2));
//
//                groupsArr.add(playlist);
//                Log.d("fetching", "groups: " + cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//
//        return groupsArr;
//    }

//    public void deleteFromGroup(int groupSno,int songSno)
//    {
//        SongGroup playlist=fetchGroup(groupSno);
//
//
////        String newMembers=playlist.getGroupMembers().replace(String.valueOf(songSno),"");
//        String[] members=playlist.getGroupMembers().split(",");
//
//        String newMembers="";
//        for(int i=0;i<members.length;i++)
//        {
//            if(members[i].equals(String.valueOf(songSno)))
//                continue;
//            else
//                newMembers+=members[i]+",";
//        }
//
//        playlist.setGroupMembers(newMembers);
//
//        Log.d("fetchinggroup", "fetchGroup: "+playlist);
//        updateGroup(playlist);
//
//    }
//
//
//
}
