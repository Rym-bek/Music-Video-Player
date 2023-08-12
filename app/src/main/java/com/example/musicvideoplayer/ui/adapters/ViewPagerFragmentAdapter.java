package com.example.musicvideoplayer.ui.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicvideoplayer.ui.fragments.FragmentMusic;
import com.example.musicvideoplayer.ui.fragments.FragmentVideo;

//адаптер для Фрагментов, фрагменты это экраны на которых отображается контент
//В проекте имеется два основных фрагменты это фрагмент музыки и видео
//данные адаптер динамически создаёт их и позволяет совершать быстрое перемещение между ними
public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

    //конструктор, необходим если нужно будет получить данные
    public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    //метод в котором создаются фрагменты
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                //создать и вернуть фрагмент музыки
                return new FragmentMusic();
            case 1:
                //создать и вернуть фрагмент видео
                return new FragmentVideo();
        }
        //создать и вернуть фрагмент музыки в случае если страница только загрузилась
        return new FragmentMusic();
    }

    //вернуть количество фрагментов
    @Override
    public int getItemCount() {
        return 2;
    }
}
