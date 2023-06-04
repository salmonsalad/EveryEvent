package com.example.everyevent.HomeEventList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.everyevent.EventInterestedView;
import com.example.everyevent.MainPage.WritePostActivity;
import com.example.everyevent.R;
import com.example.everyevent.RecentEventList.RecentEventsView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeEvent2 extends Fragment {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Fragment firstFragment;
    private Fragment secondFragment;
    private Fragment thirdFragment;
    ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         rootView= (ViewGroup) inflater.inflate(R.layout.fragment_post_list2,container,false);


         //플로팅 버튼
        FloatingActionButton floatbutton = rootView.findViewById(R.id.floatingActionButton);

        // ViewPager 설정
        viewPager = rootView.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2); //페이지 유지 개수

        // tabLayout에 ViewPager 연결
        tabLayout = rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        // Fragment 생성
        firstFragment = new PostListActivity();
        secondFragment = new RecommendEventView();
        thirdFragment = new RecentEventsView();


        // ViewPagerAdapter를 이용하여 Fragment 연결
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(firstFragment, "전체 이벤트");
        viewPagerAdapter.addFragment(secondFragment, "추천 이벤트");
        viewPagerAdapter.addFragment(thirdFragment, "최근 이벤트");
        viewPager.setAdapter(viewPagerAdapter);

        floatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(WritePostActivity.class);

            }
        });




        return rootView;
    }
    @Override
    public void onResume(){
        super.onResume();

        // ViewPager 설정
        viewPager = rootView.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2); //페이지 유지 개수

        // tabLayout에 ViewPager 연결
        tabLayout = rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        // Fragment 생성
        firstFragment = new PostListActivity();
        secondFragment = new RecommendEventView();
        thirdFragment = new RecentEventsView();
        // ViewPagerAdapter를 이용하여 Fragment 연결
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(firstFragment, "전체 이벤트");
        viewPagerAdapter.addFragment(secondFragment, "추천 이벤트");
        viewPagerAdapter.addFragment(thirdFragment, "최근 이벤트");
        viewPager.setAdapter(viewPagerAdapter);


    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }
}


