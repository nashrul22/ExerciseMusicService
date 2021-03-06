package com.example.exercisemenuservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;

import com.example.exercisemenuservice.MusicService.MusicBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import androidx.annotation.NonNull;

public class MainActivity extends Activity implements MediaPlayerControl {

    private ArrayList<Music> songList;
    private ListView songView;


    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;

    private MusicController controller;


    private boolean shuffle=false;
    private Random rand;

    private boolean paused=false, playbackPaused=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songView = (ListView) findViewById(R.id.listSong);
        songList = new ArrayList<Music>();


        MusicAdapter songAdt = new MusicAdapter(this, songList);
        songView.setAdapter(songAdt);


        Collections.sort(songList, new Comparator<Music>() {
            public int compare(Music a, Music b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        setController();
        getSongList();

        rand = new Random();

    }





    public void setController() {
        controller = new MusicController(this);

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.listSong));
        controller.setEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder) service;

            musicSrv = binder.getService();

            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            musicBound = false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shuffle:
                musicSrv.setShuffle();
                break;
            case R.id.stop:
                stopService(playIntent);
                musicSrv = null;
                System.exit(0);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv = null;
        super.onDestroy();
    }

    public void songPicked(View view) {
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if (playbackPaused){
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }


    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        musicSrv.pausePlayer();
        playbackPaused= true;
    }

    @Override
    public int getDuration() {
        if (musicSrv != null &&
                musicBound &&
                musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (musicSrv != null &&
                musicBound)
            return musicSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private void playNext() {
        musicSrv.playNext();
        if (playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    //play previous
    private void playPrev() {
        musicSrv.playPrev();
        if (playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused){
            setController();
            paused=false;
        }
    }

    public void onStop() {
        super.onStop();
        controller.hide();
    }

    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
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

