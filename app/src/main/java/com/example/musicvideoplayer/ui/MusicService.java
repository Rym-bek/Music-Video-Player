package com.example.musicvideoplayer.ui;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.musicvideoplayer.ui.models.Song;

import java.util.ArrayList;

//функция музыкального сервиса, она служит для работы с аудиозаписями
//работа с визуальной частью происходит внутри других классов
//этот класс ответственен для работы с песнями на фоне
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    //объявить переменные
    MediaPlayer mediaPlayer;
    ArrayList<Song> songs;
    private int songPosn;
    IBinder musicBind = new MusicBinder();

    //переменные сигнализирующий действие
    public static final int START = 0;
    public static final int NEXT = 1;
    public static final int PREVIOUS = 2;
    public static final int RESUME_PAUSE = 3;
    @Override
    public void onCreate() {
        super.onCreate();
        //инициализация позиции
        songPosn=0;
        initMusicPlayer();
    }
    public void initMusicPlayer(){
        //объявить экземпляр класса для проигрывания музыки
        mediaPlayer = new MediaPlayer();
        //присвоить ему необходимые атрибуты для проигрывания на фоне и в заблокированном режиме
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //объявить прослушиватели действий
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

    }

    //функция ответственная за вызов необходимых функций
    public void togglePlay(int flag) {
        switch(flag) {
            case START:
                playSong();
                break;
            case NEXT:
                playSongNext();
                break;
            case PREVIOUS:
                playSongPrevious();
                break;
            case RESUME_PAUSE:
                resumePauseSong();
                break;
        }
    }

    //запустить или остановить проигрывание
    public void resumePauseSong(){
        if(!mediaPlayer.isPlaying())
        {
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer.pause();
        }
    }

    //запустить следующую песню
    public void playSongNext(){
        songPosn++;
        if(songPosn>songs.size()-1)
        {
            songPosn=0;
        }
        playSong();
    }

    //запустить прошлую песню
    public void playSongPrevious(){
        songPosn--;
        if(songPosn<0)
        {
            songPosn=songs.size()-1;
        }
        playSong();
    }

    //основная функция, она запусат проигрывание
    public void playSong(){
        //сбросить проигрыватель
        mediaPlayer.reset();

        //получить песню исходя из выбранной позиции в списке
        Song song = songs.get(songPosn);

        //получить id
        long currSong = song.getId();

        //получить путь
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        try{
            //получить песню исходя из пути
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        //подготовить асинхронный поток для запуска
        mediaPlayer.prepareAsync();
    }

    //создать список с песнями
    public void setList(ArrayList<Song> theSongs){
        this.songs=theSongs;
    }

    //получить сервис
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    //получить позицию песню
    public int songPosition()
    {
        return songPosn;
    }

    //узнать проигрывается ли песня
    public boolean songPlaying()
    {
        return mediaPlayer.isPlaying();
    }


    //методы для работы с mediaPlayer
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    //задать текущую песню
    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }


    //приостановить трансляцию музыки
    @Override
    public boolean onUnbind(Intent intent){
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        return false;
    }
}
