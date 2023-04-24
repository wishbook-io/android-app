package com.wishbook.catalog.stories.adapter;

import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class StoriesPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> fragmentList = new ArrayList<>();

    public StoriesPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Object instantiateItem(ViewGroup arg0, int arg1) {
        return super.instantiateItem(arg0, arg1);
    }

    @Override
    public Fragment getItem(int position) {
        try {
            return fragmentList.get(position);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragments(Fragment fragment) {
        fragmentList.add(fragment);
    }


}
