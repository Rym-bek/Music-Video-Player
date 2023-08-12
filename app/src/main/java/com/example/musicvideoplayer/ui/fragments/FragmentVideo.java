package com.example.musicvideoplayer.ui.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.musicvideoplayer.R;
import com.example.musicvideoplayer.ui.adapters.AdapterSong;
import com.example.musicvideoplayer.ui.adapters.AdapterVideo;
import com.example.musicvideoplayer.ui.models.Song;
import com.example.musicvideoplayer.ui.models.Video;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//фрагмент видео
public class FragmentVideo extends Fragment {
    //объявить необходимые переменные
    List<Video> videoList = new ArrayList<Video>();
    RecyclerView videoRecycler;
    Context context;
    AdapterVideo adapterVideo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //присвоить визуальный файл фрагменту
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        //найти необходимые виджеты
        videoRecycler=view.findViewById(R.id.videoRecycler);
        //получить контекст, он небходим чтобы узнавать контекст в котором должны выполняться действия
        context=requireContext();

        //вызвать функцию загрузки видеозаписей
        loadVideoContent();
        return view;
    }
    //задать параметры для списка
    private void setVideoRecycler() {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        videoRecycler.setLayoutManager(staggeredGridLayoutManager);
    }
    //задать параметры для адаптера
    private void setVideoAdapter()
    {
        adapterVideo=new AdapterVideo(context,videoList);
        videoRecycler.setAdapter(adapterVideo);
        videoRecycler.setItemAnimator(new DefaultItemAnimator());
    }

    //функция загрузки контента, в ней вызываются остальные функции и проверяется разрешение на чтение данных из телефона
    private void loadVideoContent()
    {
        if(checkPermission())
        {
            getVideoList();
            setVideoRecycler();
            setVideoAdapter();
        }
        else
        {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    //метод который поверяет получены ли разрешенияя, в случае успеха песни добавляются на экран
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getVideoList();
                    setVideoRecycler();
                    setVideoAdapter();
                } else {
                    loadVideoContent();
                }
            });

    //проверка на то есть ли разрешение на чтение хранилища телефона
    private boolean checkPermission()
    {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    //получить список всех видеозаписей
    public void getVideoList() {
        //получить путь ко всем видеозаписяи в устройстве
        Uri collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        //вызвать класс для работы с контентом и создать запрос
        Cursor cursor = context.getContentResolver().query(collection, null, null, null, null);
        //если курсор не пустой
        if(cursor!=null)
        {
            //получить данные
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                //получить изображения видеозаписей, это нужно чтобы загружать только их картинки для предпросмотра
                Bitmap bitmap = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    try {
                        bitmap = context.getContentResolver().loadThumbnail(contentUri, new Size(640, 480), null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //присвоить данные в список
                videoList.add(new Video(contentUri, name,bitmap, duration, size));
            }
        }
    }
}
