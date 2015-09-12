package com.dujiajun.schoolsecretary;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentList;
    private String[] titles;

    public MyPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, String[] strings) {
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
        return titles[position];
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
