package com.example.musicvideoplayer.ui.models;

import android.graphics.Bitmap;
import android.net.Uri;

public class Video {
    Uri uri;
    String name;
    Bitmap bitmap;
    int duration;
    int size;

    public Video(Uri uri, String name, Bitmap bitmap, int duration, int size) {
        this.uri = uri;
        this.name = name;
        this.bitmap = bitmap;
        this.duration = duration;
        this.size = size;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
