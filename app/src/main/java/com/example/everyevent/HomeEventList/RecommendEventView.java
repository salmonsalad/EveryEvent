package com.example.everyevent.HomeEventList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.everyevent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;

public class RecommendEventView extends Fragment {
    private FirebaseFirestore firebaseFirestore;

    private static final String TAG = "RecommendEventView";

    private FloatingActionButton mWriteBoard;
    private CollectionReference collectionReference;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       ViewGroup rootView =(ViewGroup) inflater.inflate(R.layout.recommend_view,container,false);
        swipeRefreshLayout = rootView.findViewById(R.id.layout_refresh4);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DocumentReference documentReference = firebaseFirestore.collection("users").document("user_id_"+user.getUid());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                String category = task.getResult().get("category").toString();
                                refresh(category);
                            }
                        });
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.recycler_board_list4);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));




        return rootView;


    }

    public void onResume(){
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.e("bamne", user.getUid());
        DocumentReference documentReference = firebaseFirestore.collection("users").document("user_id_" + user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String category = document.get("category") != null ? document.get("category").toString() : "";
                        refresh(category);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    View.OnClickListener onClickListener = (v) -> {

    };
    public void refresh(String category) {
        //문서 가져오기
        //.orderBy("createdAt", Query.Direction.DESCENDING)
        collectionReference = firebaseFirestore.collection("posts");
        collectionReference.whereEqualTo("category", category).get()
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
