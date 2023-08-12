package com.example.musicvideoplayer.ui.models;

import android.net.Uri;

public class Song {
    long id;
    String title;
    String artist;
    Uri photo, musicURI;

    public Song(long id, String title, String artist, Uri photo, Uri musicURI) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.photo = photo;
        this.musicURI = musicURI;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public Uri getMusicURI() {
        return musicURI;
    }

    public void setMusicURI(Uri musicURI) {
        this.musicURI = musicURI;
    }
}
