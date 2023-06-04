package com.example.everyevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.everyevent.HomeEventList.PostListAdapter;
import com.example.everyevent.HomeEventList.PostListInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class EventInterestedView extends BasicActivity {
    ArrayList<String> value = new ArrayList<String>();
    @Nullable
    private FirebaseFirestore firebaseFirestore;
    private FloatingActionButton mWriteBoard;
    private CollectionReference collectionReference;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventinterestedlist);
        swipeRefreshLayout = findViewById(R.id.layout_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView =findViewById(R.id.recycler_board_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(EventInterestedView.this));
    }

    public void onResume(){
        super.onResume();
        refresh();

    }

    View.OnClickListener onClickListener = (v) -> {

    };
    public void refresh() {
        //우선 유저 컬렉션에 들어가서 유저 문서 불러온 후 유저 문서의 관심 이벤트 필드의 어레이 리스트를 가져옴

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userPath = firebaseFirestore.collection("users").document("user_id_"+user.getUid());
        userPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document1 = task.getResult();
                    if (document1.exists()) {
                        ArrayList<String> interestedEvents = (ArrayList<String>) document1.getData().get("interestedEvents");
                        if (interestedEvents != null) {
                            ArrayList<String> interested = new ArrayList<String>();
                            //가져온 후 for 문으로 interested 어레이 리스트에 옮김
                            for (String str : interestedEvents) {
                                interested.add(str);
                            }
                            ArrayList<PostListInfo> postList = new ArrayList<>();
                            //이후, interested 리스트에 있는 문서 id 와 일치하는 문서들을 post 컬랙션에서 찾아서 각 필드의 데이터들을 가져온 후 출력
                            for (String str : interested) {
                                DocumentReference postPath = firebaseFirestore.document("posts/"+str);

                                postPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {

                                            DocumentSnapshot document2 = task.getResult();

                                            long numberOfApplicants = (long) document2.getData().get("numberOfApplicants");
                                            long numberOfAttendees = (long) document2.getData().get("numberOfAttendees");
                                            try {
                                                postList.add(new PostListInfo(
                                                                document2.getData().get("userInfo").toString(),
                                                                document2.getData().get("title").toString(),
                                                                document2.getData().get("contents").toString(),
                                                                document2.getData().get("address").toString(),
                                                                new Date(document2.getDate("startDate").getTime()),
                                                                document2.getData().get("numberOfPeopleCanApply").toString(),
                                                                document2.getData().get("documentId").toString(),
                                                                numberOfApplicants,
                                                                numberOfAttendees


                                                        )
                                                );
                                                //리사이클러 뷰 초기화
                                                PostListAdapter mAdapter = new PostListAdapter(EventInterestedView.this, postList);
                                                recyclerView.setAdapter(mAdapter);
                                            } catch (ParseException e) {
                                                throw new RuntimeException(e);
                                            }


                                        }
                                    }

                                });



                            }

                        }
                    }
                }
            }
        });
    }

    public void myStartActivity(Class c){
        Intent intent = new Intent(EventInterestedView.this, c);
        startActivity(intent);
    }


    }



