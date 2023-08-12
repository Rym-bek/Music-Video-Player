package com.example.musicvideoplayer.ui.interfaces;

import android.net.Uri;

public interface UpdateSong {
    public void update(long id, int positionId, String title, String artist, Uri photo, Uri musicURI);
}