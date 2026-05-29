package com.example.myapplicationworld.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplicationworld.fragments.ConfiguracionFragment;
import com.example.myapplicationworld.fragments.HistorialFragment;
import com.example.myapplicationworld.fragments.PrestamoFragment;
import com.example.myapplicationworld.fragments.RecepcionFragment;

public class DashboardPagerAdapter extends FragmentStateAdapter
{

    public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // En este punto se hace necesario el BACKEND (java)
        switch (position){
            case 0: return new PrestamoFragment();
            case 1: return new RecepcionFragment();
            case 2: return new HistorialFragment();
            default: return new ConfiguracionFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
