package com.thegroup.rebuild.thegroupalpha;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public  class MyAdapter extends FragmentStatePagerAdapter {
    List<String> titles;
    List<Fragment> fragments;
    public MyAdapter(FragmentManager fragmentManager, List<String> titles, List<Fragment> fragments){
        super(fragmentManager);
        this.titles=titles;
        this.fragments=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}