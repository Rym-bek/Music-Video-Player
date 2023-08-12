package com.example.musicvideoplayer.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicvideoplayer.R;
import com.example.musicvideoplayer.ui.interfaces.UpdateSong;
import com.example.musicvideoplayer.ui.models.Song;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//адаптер песен, он нужен чтобы адаптировать данные песен под список
public class AdapterSong extends RecyclerView.Adapter<AdapterSong.SongViewHolder> {

    //объявление необходимых переменных
    Context context;
    List<Song> listSong;
    SongViewHolder holderPrevious;
    UpdateSong updateSong;
    SongViewHolder songViewHolder;
    private List<SongViewHolder> viewHolderList = new ArrayList<>();

    //создание конструктора, он нужен чтобы передать данные в адаптер
    public AdapterSong(Context context, List<Song> listSong, UpdateSong updateSong) {
        this.context = context;
        this.listSong = listSong;
        this.updateSong = updateSong;
    }


    //метод которые вызывается при создании, здесь макет раздувается, и список приобретает небходимый вид
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //раздуть макет с помощью inflate
        View songItems= LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
        SongViewHolder songViewHolder = new SongViewHolder(songItems);
        //записать все viewHolder, это пригодится в последующем, чтобы менять цвет проигрываемой музыки
        viewHolderList.add(songViewHolder);
        //вернуть нужный предмет
        return new SongViewHolder(songItems);
    }

    //этод метод вызывается при показе списка на экране, загружаются лишь те что видны в поле зрения
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //получить необходимые данные
        songViewHolder=holder;
        Song song = listSong.get(position);
        long id=song.getId();
        String title=song.getTitle();
        String artist=song.getArtist();
        Uri photo =song.getPhoto();
        Uri musicURI=song.getMusicURI();
        //присвоить данные нужным элементам по id в списке
        holder.shapeableImageView_MusicImage.setImageURI(photo);
        holder.materialTextView_musicName.setText(title);
        holder.materialTextView_author.setText(artist);

        //если на элемент нажали
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //обновить текущую песню
                updateSong.update(id,position,title,artist,photo,musicURI);
                //изменить цвет текста
                if(holderPrevious!=null)
                {
                    holderPrevious.materialTextView_musicName.setTextColor(context.getResources().getColor(R.color.black));
                    holderPrevious.materialTextView_author.setTextColor(context.getResources().getColor(R.color.black));
                }
                holder.materialTextView_musicName.setTextColor(androidx.appcompat.R.attr.colorPrimary);
                holder.materialTextView_author.setTextColor(androidx.appcompat.R.attr.colorPrimary);

                //запомнить предыдущий отмеченный вариант
                holderPrevious=holder;
            }
        });
    }

    @Override
    public int getItemCount() {
        //получить длину списка всех песен
        return listSong.size();
    }

    //обновить цвета текущей проигрывающейся композиции
    public void updateColors(int position) {
        viewHolderList.get(position).materialTextView_musicName.setTextColor(androidx.appcompat.R.attr.colorPrimary);
        viewHolderList.get(position).materialTextView_author.setTextColor(androidx.appcompat.R.attr.colorPrimary);
        holderPrevious.materialTextView_musicName.setTextColor(context.getResources().getColor(R.color.black));
        holderPrevious.materialTextView_author.setTextColor(context.getResources().getColor(R.color.black));
        holderPrevious=viewHolderList.get(position);
    }

    //метод который получает элементы из списка
    //Расширенный список можно содержать несколько элементов внутри одного экземпляра
    //именно поэтому были использованы модели данных
    public static final class SongViewHolder extends RecyclerView.ViewHolder{
        //получить виджеты
        ShapeableImageView shapeableImageView_MusicImage;
        public MaterialTextView materialTextView_musicName;
        MaterialTextView materialTextView_author;
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            //получить виджеты по их id
            shapeableImageView_MusicImage=itemView.findViewById(R.id.shapeableImageView_MusicImage);
            materialTextView_musicName=itemView.findViewById(R.id.materialTextView_musicName);
            materialTextView_author=itemView.findViewById(R.id.materialTextView_author);
        }
    }
}
