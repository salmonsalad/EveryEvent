package com.example.everyevent.HomeEventList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.everyevent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class PostListActivity extends Fragment {
    @Nullable
    private FirebaseFirestore firebaseFirestore;
    private FloatingActionButton mWriteBoard;
    private CollectionReference collectionReference;

    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.post_list_view,container,false);
        swipeRefreshLayout = rootView.findViewById(R.id.layout_refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });






        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.recycler_board_list2);
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
        //문서 가져오기
        //.orderBy("createdAt", Query.Direction.DESCENDING)
        collectionReference = firebaseFirestore.collection("posts");
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<PostListInfo> postList = new ArrayList<>();

                            for(QueryDocumentSnapshot document : task.getResult()){
                                long numberOfApplicants = (long)document.getData().get("numberOfApplicants");
                                long numberOfAttendees = (long)document.getData().get("numberOfAttendees");
                                try {
                                    postList.add(new PostListInfo(
                                                    document.getData().get("userInfo").toString(),
                                                    document.getData().get("title").toString(),
                                                    document.getData().get("contents").toString(),
                                                    document.getData().get("address").toString(),
                                                    new Date(document.getDate("startDate").getTime()),
                                                    document.getData().get("numberOfPeopleCanApply").toString(),
                                                    document.getData().get("documentId").toString(),
                                                    numberOfApplicants,
                                                    numberOfAttendees


                                            )
                                    );
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }


                            }
                            //리사이클러 뷰 초기화
                            PostListAdapter mAdapter = new PostListAdapter(getActivity(), postList);
                            recyclerView.setAdapter(mAdapter);

                        }
                    }
                });
    }

    public void myStartActivity(Class c){
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }

}