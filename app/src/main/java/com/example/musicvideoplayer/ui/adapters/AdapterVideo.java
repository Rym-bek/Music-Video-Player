package com.example.musicvideoplayer.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.musicvideoplayer.R;
import com.example.musicvideoplayer.ui.ShowVideoActivity;
import com.example.musicvideoplayer.ui.models.Song;
import com.example.musicvideoplayer.ui.models.Video;

import java.util.ArrayList;
import java.util.List;

//адаптер видео, он нужен чтобы адаптировать данные видео под список
public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.VideoViewHolder> {
    //объявление необходимых переменных
    Context context;
    List<Video> listVideo;
    //создание конструктора, он нужен чтобы передать данные в адаптер
    public AdapterVideo(Context context, List<Video>listVideo)
    {
        this.context=context;
        this.listVideo=listVideo;
    }

    //метод которые вызывается при создании, здесь макет раздувается, и список приобретает небходимый вид
    @NonNull
    @Override
    public AdapterVideo.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //раздуть макет с помощью inflate
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        //вернуть нужный предмет
        return new VideoViewHolder(view);
    }

    //этод метод вызывается при показе списка на экране, загружаются лишь те что видны в поле зрения
    @Override
    public void onBindViewHolder(@NonNull AdapterVideo.VideoViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //присвоить данные нужным элементам по id в списке
        holder.appCompatImageView_VideoImage.setImageBitmap(listVideo.get(position).getBitmap());

        //если на элемент нажали
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //осуществляется переход на страницу просмотра видео
                Intent intent = new Intent(context, ShowVideoActivity.class);
                intent.putExtra("videoUri",listVideo.get(position).getUri().toString());
                context.startActivity(intent);
            }
        });
    }

    //метод который получает элементы из списка
    //Расширенный список можно содержать несколько элементов внутри одного экземпляра
    //именно поэтому были использованы модели данных
    public static final class VideoViewHolder extends RecyclerView.ViewHolder
    {
        //получить виджеты
        AppCompatImageView appCompatImageView_VideoImage;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            //получить виджеты по их id
            appCompatImageView_VideoImage=itemView.findViewById(R.id.appCompatImageView_VideoImage);
        }
    }

    @Override
    public int getItemCount() {
        //получить длину списка всех видео
        return listVideo.size();
    }
}
