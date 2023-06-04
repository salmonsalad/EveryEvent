package com.example.everyevent.HomeEventList;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private ArrayList<String> fragmentTitle = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override   // 화면의 실제 Fragment를 반환한다.
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override   // Page수
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override   // 제목
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }

    public void addFragment (Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitle.add(title);
    }
}