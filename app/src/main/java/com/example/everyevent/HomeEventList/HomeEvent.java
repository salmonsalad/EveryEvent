package com.example.everyevent.HomeEventList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.everyevent.R;
import com.example.everyevent.RecentEventList.RecentEventsView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeEvent extends Fragment {
    Fragment postListActivity = new PostListActivity();
    Fragment recentEventView = new RecentEventsView();
    final int NUM_PAGES = 2;
    ViewGroup rootView;
    ViewPager2 viewPager2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_post_list,container,false);





        // 탭과 뷰페이저 연결
        TabLayout tabLayout = rootView.findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position == 0) {
                    tab.setText("Feed");
                } else {
                    tab.setText("Artist");
                }
            }
        });
        viewPager2.setAdapter(new viewPagerAdapter(this));
        viewPager2.setCurrentItem(0);
        tabLayoutMediator.attach();

        return rootView;
    }

    // 뷰페이저2 어댑터
    private class viewPagerAdapter extends FragmentStateAdapter {
        public viewPagerAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 0) {
                return postListActivity;
            } else {
                return recentEventView;
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
    public void onResume(){
        //어답터설정
        super.onResume();
        viewPager2.setAdapter(new viewPagerAdapter(this));
        viewPager2.setCurrentItem(0);

    }
}


