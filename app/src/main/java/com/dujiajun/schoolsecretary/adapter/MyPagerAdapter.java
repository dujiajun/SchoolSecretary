package com.dujiajun.schoolsecretary.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentList;
    private ArrayList<String> titles;
    private int mCount;

    public MyPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, ArrayList<String> strings) {
        super(fm);
        this.fragmentList = fragments;
        this.titles = strings;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    /*@Override
    public void notifyDataSetChanged() {
        mCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }*/

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
