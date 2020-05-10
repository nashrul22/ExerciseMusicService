package com.example.exercisemenuservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicAdapter extends BaseAdapter {
    private ArrayList<Music> songs;
    private LayoutInflater songInf;


    public MusicAdapter(Context c, ArrayList<Music> theSongs){
        songs = theSongs;
        songInf = LayoutInflater.from(c);

    }


    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout songPlay = (LinearLayout) songInf.inflate(
                R.layout.activity_music, parent, false
        );
        TextView songView = (TextView) songPlay.findViewById(R.id.musicTitle);
        TextView artisView = (TextView) songPlay.findViewById(R.id.musicArtis);

        Music currSong = songs.get(position);

        songView.setText(currSong.getTitle());
        artisView.setText(currSong.getArtis());

        songPlay.setTag(position);

        return songPlay;
    }
}
