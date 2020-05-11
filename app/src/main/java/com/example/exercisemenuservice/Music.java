package com.example.exercisemenuservice;


public class Music {
    private long id;
    private String title;
    private String artis;

    public Music(long songID, String songTitle, String songArtis) {
        id = songID;
        title = songTitle;
        artis = songArtis;
    }

    public long getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtis() {
        return artis;
    }
}
