package com.example.everyevent.RecentEventList;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.everyevent.R;
import com.example.everyevent.RecentEventList.RecentList;
import com.example.everyevent.RecentEventList.RecentListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class RecentEventsView extends Fragment {

    @Nullable
    private FirebaseFirestore firebaseFirestore;
    private SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<RecentList> postList = new ArrayList<>();
    ArrayList<RecentList> postListView = new ArrayList<>();
    RecyclerView recyclerView;
    boolean update;

    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.recent_event_list,container,false);
        swipeRefreshLayout = rootView.findViewById(R.id.layout_refresh3);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView =rootView.findViewById(R.id.recycler_board_list3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
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
                    if (postList.size() != 0) {
                        postList.clear();
                    }
                    if (postListView.size() != 0) {
                        postListView.clear();
                    }
                    DocumentSnapshot document1 = task.getResult();
                    if (document1.exists()) {
                        ArrayList<String> recentEvents = (ArrayList<String>) document1.getData().get("recentEvents");
                        if (recentEvents != null) {
                            //이후, interested 리스트에 있는 문서 id 와 일치하는 문서들을 post 컬랙션에서 찾아서 각 필드의 데이터들을 가져온 후 출력
                            for (int i = recentEvents.size() - 1; i > -1; i--) {

                                    String str = recentEvents.get(i);
                                    DocumentReference postPath = firebaseFirestore.collection("posts").document(str);
                                    int finalI = i;
                                    postPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document2 = task.getResult();
                                                if(task.getResult()!=null){
                                                    long numberOfApplicants = Long.parseLong((String) document2.getData().get("numberOfPeopleCanApply"));
                                                    long numberOfAttendees = (long) document2.getData().get("numberOfAttendees");
                                                    String userInfo;
                                                    try {
                                                        postList.add(new RecentList(
                                                                        finalI,
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
                                                        Log.e("size", postList.size() + "");
                                                        Log.e("Size",postList.size()+"");
                                                        //리사이클러 뷰 초기화
                                                        RecentListAdapter mAdapter = new RecentListAdapter(getActivity(), postList);
                                                        recyclerView.setAdapter(mAdapter);
                                                    } catch (ParseException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }else {

                                                    recentEvents.remove(str);

                                                    userPath
                                                            .update("recentEvents",recentEvents)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w(TAG, "Error updating document", e);
                                                                }
                                                            });

                                                }


                                            }


                                        }
                                    });
                                if (recentEvents.size() == postList.size()) {
                                    for (RecentList list : postList) {
                                        postListView.set(list.getIndex(), list);
                                        Log.e("id", postListView.get(0).getDocumentId() + postListView.get(1).getDocumentId() + postListView.get(2).getDocumentId() + postListView.get(3).getDocumentId());
                                    }

                                }

                            }

                        }


                    } else {
                        RecentListAdapter mAdapter = new RecentListAdapter(getActivity(), postList);
                        recyclerView.setAdapter(mAdapter);
                    }
                }
            }
        });
    }

    public void myStartActivity(Class c){
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }


}




