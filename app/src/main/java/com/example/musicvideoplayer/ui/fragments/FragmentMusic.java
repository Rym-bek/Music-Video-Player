package com.example.musicvideoplayer.ui.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicvideoplayer.R;
import com.example.musicvideoplayer.ui.MusicService;
import com.example.musicvideoplayer.ui.adapters.AdapterSong;
import com.example.musicvideoplayer.ui.interfaces.UpdateSong;
import com.example.musicvideoplayer.ui.models.Song;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import com.example.musicvideoplayer.ui.MusicService.MusicBinder;

//фрагмент музыки
public class FragmentMusic extends Fragment implements UpdateSong{
    //объявить необходимые переменные
    private ArrayList<Song> songList = new ArrayList<>();
    private RecyclerView musicRecycler;
    AdapterSong adapterSong;
    Context context;
    ShapeableImageView shapeableImageView_controlPanel_MusicImage;
    MaterialTextView materialTextView_musicName_controlPanel,materialTextView_author_controlPanel;
    AppCompatImageButton song_next,song_startStop,song_back;
    private MusicService musicService=new MusicService();
    private Intent playIntent;
    private boolean musicBound=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //присвоить визуальный файл фрагменту
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        //найти необходимые виджеты
        musicRecycler=view.findViewById(R.id.videoRecycler);
        shapeableImageView_controlPanel_MusicImage=view.findViewById(R.id.shapeableImageView_controlPanel_MusicImage);
        materialTextView_musicName_controlPanel=view.findViewById(R.id.materialTextView_musicName_controlPanel);
        materialTextView_author_controlPanel=view.findViewById(R.id.materialTextView_author_controlPanel);
        song_next=view.findViewById(R.id.song_next);
        song_startStop=view.findViewById(R.id.song_startStop);
        song_back=view.findViewById(R.id.song_back);

        //получить контекст, он небходим чтобы узнавать контекст в котором должны выполняться действия
        context=requireContext();

        //вызвать функцию загрузки данных
        loadContent();

        //вызвать сервис для работы с музыкой
        if(playIntent==null){
            playIntent = new Intent(context, MusicService.class);
            context.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            context.startService(playIntent);
        }

        //кнопка запуска и присотановки песни
        song_startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //вызвать метод в музыкальном сервисе
                musicService.togglePlay(MusicService.RESUME_PAUSE);
                //если музыка играет изменить значок на стоп
                if(musicService.songPlaying())
                {
                    song_startStop.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_pause_24));
                }
                else
                {
                    song_startStop.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_play_arrow_24));
                }
            }
        });

        //кнопка запускающая следующую песню
        song_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //метод в классе музыкального сервиса который перематывает песню
                musicService.togglePlay(MusicService.NEXT);
                //изменить значок на стоп
                song_startStop.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_pause_24));
                //обновить цвет выбранной песни
                adapterSong.updateColors(musicService.songPosition());
            }
        });

        //то же самое для кнопки назад, только в обратную сторону
        song_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.togglePlay(MusicService.PREVIOUS);
                song_startStop.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_pause_24));
                adapterSong.updateColors(musicService.songPosition());
            }
        });
        return view;
    }

    //если музыка выбрана то запустить проигрывание песни
    public void songPicked(int pos){
        musicService.setSong(pos);
        musicService.togglePlay(MusicService.START);
    }

    //подключить сервис для работы с музыкой
    ServiceConnection musicConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicBinder binder = (MusicBinder)service;
            musicService = binder.getService();
            musicService.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    //функция загрузки контента, в ней вызываются остальные функции и проверяется разрешение на чтение данных из телефона
    private void loadContent()
    {
        if(checkPermission())
        {
            getMusicList();
            setRecycler();
            setAdapter();
        }
        else
        {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    //при закрытии приложения музыка прекращается
    @Override
    public void onDestroy() {
        super.onDestroy();
        context.stopService(playIntent);
        musicService=null;
    }

    //метод который поверяет получены ли разрешенияя, в случае успеха песни добавляются на экран
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getMusicList();
                    setRecycler();
                    setAdapter();
                } else {
                    loadContent();
                }
            });

    //проверка на то есть ли разрешение на чтение хранилища телефона
    private boolean checkPermission()
    {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    //задать параметры для списка
    private void setRecycler() {
        musicRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        musicRecycler.setLayoutManager(layoutManager);
    }

    //задать параметры для адаптера
    private void setAdapter()
    {
        adapterSong=new AdapterSong(context,songList,this);
        musicRecycler.setAdapter(adapterSong);
        musicRecycler.setItemAnimator(new DefaultItemAnimator());
    }

    //получить список всех песен
    public void getMusicList() {
        //вызвать класс для работы с контентом в андройд
        ContentResolver musicResolver = context.getContentResolver();
        //получить путь ко всем песням в устройстве
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //создать запрос
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        //если он не пустой
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //получить данные
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            do {
                int thisId = musicCursor.getInt(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                Uri contentUri = ContentUris.withAppendedId(
                        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);
                //присвоить данные в список
                songList.add(new Song(thisId, thisTitle, thisArtist,null,contentUri));
            }
            while (musicCursor.moveToNext());
            //закрыть курсор чтобы не было утечек памяти
            musicCursor.close();
        }
    }

    //интерфейс, который передаёт данные из адаптера в это окно и запускает проигрывание
    @Override
    public void update(long idSong, int positionIdSong, String titleSong, String artistSong, Uri photoSong, Uri uriSong) {
        shapeableImageView_controlPanel_MusicImage.setImageURI(photoSong);
        materialTextView_musicName_controlPanel.setText(titleSong);
        materialTextView_author_controlPanel.setText(artistSong);
        song_startStop.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_pause_24));
        songPicked(positionIdSong);
    }
}
