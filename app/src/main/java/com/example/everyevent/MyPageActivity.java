package com.example.everyevent;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.everyevent.Login.SignUpActivity;
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

public class MyPageActivity extends Fragment {
    FirebaseUser user;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();
    StorageReference riversRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.user_mypage,container,false);

        rootView.findViewById(R.id.logout).setOnClickListener(onClickListener);
        rootView.findViewById(R.id.update_userInfo).setOnClickListener(onClickListener);
        rootView.findViewById(R.id.interested_event_button).setOnClickListener(onClickListener);
        rootView.findViewById(R.id.manage_generated_events).setOnClickListener(onClickListener);
        ImageView user_image = rootView.findViewById(R.id.user_image);
       TextView userName = rootView.findViewById(R.id.user_name_in);
       TextView userAddress = rootView.findViewById(R.id.myAddress);
       TextView birthDay = rootView.findViewById(R.id.birthDayNum);
        TextView phoneNum = rootView.findViewById(R.id.phonenumber);

        riversRef = storageRef.child("images/"+user.getUid()+"/profileImage.jpg");

        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(MyPageActivity.this).load(uri).into(user_image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        DocumentReference userPath = firebaseFirestore.collection("users").document("user_id_"+user.getUid());
        userPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document1 = task.getResult();
                    if (document1.exists()) {
                        userName.setText(document1.getData().get("name").toString());
                        phoneNum.setText(document1.getData().get("phoneNumber").toString());
                        birthDay.setText(document1.getData().get("birthDay").toString());
                        userAddress.setText(document1.getData().get("address").toString());

                    }
                }else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        return rootView;
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.update_userInfo:
                    myStartActivity(UserInfoView.class);
                    break;
                case R.id.interested_event_button:
                    myStartActivity(EventInterestedView.class);
                    break;
                case R.id.manage_generated_events:
                    myStartActivity(ManageGeneratedEvents.class);
                    break;
                case R.id.logout:
                    myStartActivity(SignUpActivity.class);
                    break;

            }
        }
    };

    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }

}
