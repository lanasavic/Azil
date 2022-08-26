package com.example.azil.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.azil.Fragments.BreedFragment;
import com.example.azil.Fragments.LocationFragment;
import com.example.azil.Fragments.SpeciesFragment;
import com.example.azil.Fragments.TimeFragment;

public class ViewPager2Adapter extends FragmentStatePagerAdapter {

    public ViewPager2Adapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SpeciesFragment();
            case 1:
                return new BreedFragment();
            case 2:
                return new LocationFragment();
            case 3:
                return new TimeFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}