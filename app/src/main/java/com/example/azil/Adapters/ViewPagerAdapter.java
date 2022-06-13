package com.example.azil.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.azil.Fragments.AnimalsFragment;
import com.example.azil.Fragments.DonationsFragment;
import com.example.azil.Fragments.ShelterFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ShelterFragment();
            case 1:
                return new AnimalsFragment();
            case 2:
                return new DonationsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}