package com.example.musicvideoplayer.ui;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicvideoplayer.R;

//активность показа видео
public class ShowVideoActivity extends AppCompatActivity {
    //объявить виджеты
    VideoView videoView_videoPlayer;
    Uri myVideoUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //присвоить файл представления
        setContentView(R.layout.show_video_activity);
        //получить виджеты
        videoView_videoPlayer=findViewById(R.id.videoView_videoPlayer);

        //получить путь видеозаписи в устройстве
        myVideoUri = Uri.parse(getIntent().getStringExtra("videoUri"));
        if(myVideoUri!=null)
        {
            //запустить показ видеозаписи
            videoView_videoPlayer.setVideoURI(myVideoUri);
            videoView_videoPlayer.start();
        }
    }
}
