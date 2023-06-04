package com.example.everyevent.RecentEventList;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.everyevent.R;
import com.example.everyevent.ShowPostList.ShowPostActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class RecentListAdapter extends RecyclerView.Adapter<RecentListAdapter.RecentListViewHolder> {
    private final ArrayList<RecentList> mDataset;
    private final Activity activity;
    private String documentId;
    StorageReference riversRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    public static class RecentListViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        public RecentListViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public RecentListAdapter(Activity activity, ArrayList<RecentList> myDataset){
        mDataset = myDataset;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final RecentListViewHolder recentListViewHolder = new RecentListViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                documentId = mDataset.get(recentListViewHolder.getAdapterPosition()).getDocumentId();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DocumentReference userPath = firebaseFirestore.collection("users").document("user_id_"+user.getUid());
                userPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ArrayList<String> recentEvents = (ArrayList<String>) document.getData().get("recentEvents");
                                if(recentEvents!=null){
                                    if(!recentEvents.contains(documentId)){
                                        if(recentEvents.size()<=10) {
                                            recentEvents.add(documentId);
                                        }else{
                                            recentEvents.remove(0);
                                            recentEvents.add(documentId);
                                        }

                                    }

                                    }else{
                                    recentEvents = new ArrayList<String>();
                                    recentEvents.add(documentId);
                                }



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
                myStartActivity(ShowPostActivity.class);

            }
        });
        return recentListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecentListViewHolder holder, int position){
        CardView cardView = holder.cardView;
        long diff = 0;
        try {

            Date start = mDataset.get(position).getStartDate();
            Date date = new Date();
            diff = start.getTime() - date.getTime();
            //difference = time.convert(diff, TimeUnit.MILLISECONDS);
            if (diff > 0) {
                mDataset.get(position).setRunning(false);
            } else {
                mDataset.get(position).setRunning(true);
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        documentId = mDataset.get(position).getDocumentId();

        riversRef = storageRef.child("images/"+documentId+"/eventImage.jpg");

        //이미지
        ImageView eventImage = cardView.findViewById(R.id.user_image);
        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                Glide.with(activity).load(uri).into(eventImage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


        //제목
        TextView titleTextView = cardView.findViewById(R.id.eventTitle);
        titleTextView.setText(mDataset.get(position).getTitle());

        //이벤트 장소
        TextView userName = cardView.findViewById(R.id.myAddress);
        userName.setText(mDataset.get(position).getAddress());

        TextView people = cardView.findViewById(R.id.numberOfPeople);
        if(mDataset.get(position).getRunning()==true){
            //이벤트 출석 인원
            people.setText(mDataset.get(position).getNumberOfAttendees()+"");
        }else{
            //이벤트 신청 인원
            people.setText(mDataset.get(position).getNumberOfApplicants() + "");
        }


        //이벤트 수용 가능 인원
        TextView numberOfPeopleCanApply = cardView.findViewById(R.id.numberOfPeopleCanApply);
        numberOfPeopleCanApply.setText(mDataset.get(position).getNumberOfPeopleCanApply());

        //이벤트 상태 (실행중/남은 날짜)
        if(mDataset.get(position).getRunning() == true){
            //이벤트 실행중
            TextView startDate = cardView.findViewById(R.id.eventState);
            startDate.setText("실행중");
        }else{
            //이벤트 남은 날짜
            TextView startDate = cardView.findViewById(R.id.eventState);
            startDate.setText("남은 날짜 : "+diff/(60*60*24*1000));


        }


        //컨텐츠
        /*ArrayList<String> contentsList = mDataset.get(position).getMain_content();
        LinearLayout baseLayout = cardView.findViewById(R.id.layout_base);
        LinearLayout contentsLayout = baseLayout.findViewById(R.id.layout_contents);
        TextView contentTextView = contentsLayout.findViewById(R.id.text_content);
        contentTextView.setText(contentsList.get(0));

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int contentsSize = contentsList.size();
        if (contentsSize != 1) {
            String contents = contentsList.get(1);
            if (Patterns.WEB_URL.matcher(contents).matches()) {
                ImageView imageView = new ImageView(activity);
                imageView.setLayoutParams(layoutParams);
                baseLayout.addView(imageView);
                Glide.with(activity).load(contents).override(imageSize).into(imageView);
            }
        }*/
    }
    public void removeItem(int position) {

    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(activity, c);
        intent.putExtra("documentId",documentId);
        activity.startActivity(intent);
    }
}
