package com.example.everyevent.MainPage;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.everyevent.ApplyedEventView;
import com.example.everyevent.BasicActivity;
import com.example.everyevent.EventInterestedView;
import com.example.everyevent.HomeEventList.HomeEvent;
import com.example.everyevent.HomeEventList.HomeEvent2;
import com.example.everyevent.HomeEventList.PostListActivity;
import com.example.everyevent.Login.MemberInitActivity;
import com.example.everyevent.Login.SignUpActivity;
import com.example.everyevent.ManageGeneratedEvents;
import com.example.everyevent.MyPageActivity;
import com.example.everyevent.R;
import com.example.everyevent.RecentEventList.RecentEventsView;
import com.example.everyevent.UserInfoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

public class MainActivity extends BasicActivity {

    CreateQRActivity createQRActivity;
    Fragment myPageActivity;

    Fragment homeEvent;

    private static final String TAG = "MainActivity";
    private LinearLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if(user == null){
            myStartActivity(SignUpActivity.class);
        }else{
            //myStartActivity(MemberInitActivity.class);
            //myStartActivity(CameraActivity.class);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document("user_id_"+user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document!=null) {
                            if (document.exists()) {
                                Log.d("gt", "DocumentSnapshot data: " + document.getData());
                            } else {
                                myStartActivity(MemberInitActivity.class);
                                Log.d("gt", "No such document");
                            }

                        }

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
            //회원가입 or 로그인

        }
        myPageActivity = new MyPageActivity();
        createQRActivity = new CreateQRActivity();
        homeEvent = new HomeEvent2();
        container = findViewById(R.id.layout_fragment);

        findViewById(R.id.QRButton1).setOnClickListener(onClickListener);
        findViewById(R.id.QRButton2).setOnClickListener(onClickListener);
        findViewById(R.id.QRcode1).setOnClickListener(onClickListener);
        findViewById(R.id.QRcode2).setOnClickListener(onClickListener);
        LinearLayout layout = (LinearLayout) findViewById(R.id.HomeButton1);
        findViewById(R.id.HomeButton2).setOnClickListener(onClickListener);
        layout.setOnClickListener(onClickListener);
        findViewById(R.id.introduction_button1).setOnClickListener(onClickListener);
        findViewById(R.id.introduction_button2).setOnClickListener(onClickListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment,homeEvent).commit();

    }
//    View.OnClickListener onClickListener = new View.OnClickListener(){
//        @Override
//        public void onClick(View v) {
//            switch(v.getId()) {
//                case R.id.QRButton1:
//                case R.id.QRButton2:
//                    if(createQRActivity == null){
//                        createQRActivity = new CreateQRActivity();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment,createQRActivity).commit();
//                    }
//                    if(createQRActivity != null) {
//                        getSupportFragmentManager().beginTransaction().show(createQRActivity).commit();
//                    }
//                    if(homeEvent != null){
//                        getSupportFragmentManager().beginTransaction().hide(homeEvent).commit();
//                    }
//                    break;
//
//                case R.id.CommunityButton1:
//                case R.id.CommunityButton2:
//
//                    break;
//                case R.id.announcementButton1:
//
//                    break;
//                case R.id.HomeButton1:
//                case R.id.HomeButton2:
//                    if(homeEvent == null){
//                        homeEvent = new HomeEvent2();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment,homeEvent).commit();
//                    }
//                    if(homeEvent != null) {
//                        getSupportFragmentManager().beginTransaction().show(homeEvent).commit();
//                    }
//                    if(createQRActivity != null){
//                        getSupportFragmentManager().beginTransaction().hide(createQRActivity).commit();
//                    }
//                    break;
//            }
//
//
//
//        }
//    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.QRButton1:
                case R.id.QRButton2:
                    getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment, myPageActivity).commit();
                    break;

                case R.id.QRcode1:
                case R.id.QRcode2:
                    getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment, createQRActivity).commit();
                    break;

                case R.id.introduction_button1:
                case R.id.introduction_button2:
                    // 여기에 AppIntroduceActivity로 이동하는 코드를 추가하세요
                    myStartActivity(AppIntroduceActivity.class);
                    break;

                case R.id.HomeButton1:
                case R.id.HomeButton2:
                    getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment, homeEvent).commit();
                    break;
            }
        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.person_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int curld = item.getItemId();
        switch(curld) {
            case R.id.search_action:
                myStartActivity(SearchEvents.class);
                break;



        }
        return super.onOptionsItemSelected(item);
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }



}