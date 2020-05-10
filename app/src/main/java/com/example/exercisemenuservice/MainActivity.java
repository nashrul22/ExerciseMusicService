package com.example.exercisemenuservice;

import androidx.appcompat.app.AppCompatActivity;
import com.example.exercisemenuservice.MusicService.MusicBinder;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.widget.ListView;
import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Music> songList;
    private ListView songView;


    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songView = (ListView) findViewById(R.id.listSong);
        songList = new ArrayList<Music>();


        MusicAdapter songAdt = new MusicAdapter(this, songList);
        songView.setAdapter(songAdt);

        getMusicList();
        Collections.sort(songList, new Comparator<Music>(){
           public int compare(Music a, Music b){
               return a.getTitle().compareTo(b.getTitle());
           }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;

            musicSrv = binder.getService();

            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
musicBound = false;
        }
    };


    public void getMusicList(){
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()){
            int titleColumn = musicCursor.getColumnIndex(
                    android.provider.MediaStore.Audio.Media.TITLE
            );
            int idColumn = musicCursor.getColumnIndex(
                    android.provider.MediaStore.Audio.Media._ID
            );
            int artisColumn = musicCursor.getColumnIndex(
                    android.provider.MediaStore.Audio.Media.ARTIST
            );

            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtis = musicCursor.getString(artisColumn);
                songList.add(new Music(thisId, thisTitle, thisArtis));
            }
            while (musicCursor.moveToNext());
        }

    }

}
