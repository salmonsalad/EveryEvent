package com.example.everyevent;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class UserInfoView extends AppCompatActivity {
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();
    StorageReference riversRef;


    // Note that in the URL, characters are URL escaped!

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_view);
        String email = "";
         user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // email address
            email = user.getEmail();


        }
        riversRef = storageRef.child("images/"+user.getUid()+"/profileImage.jpg");
        TextView userCategory = findViewById(R.id.userCategory);
        TextView userEmail = findViewById(R.id.userEmailAddressText);
        TextView userName = findViewById(R.id.userNameText);
        TextView userPhone = findViewById(R.id.userPhoneNumber);
        TextView userBirthDay = findViewById(R.id.userBirthDay);
        TextView userAddress = findViewById(R.id.userAddress);
        Button button = findViewById(R.id.updateButton);
        ImageView profileImage = findViewById(R.id.imageView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(MemberInfoUpdateActivity.class);
            }
        });
        userEmail.setText(email);
        DocumentReference userPath = firebaseFirestore.collection("users").document("user_id_"+user.getUid());
        userPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document1 = task.getResult();
                    if (document1.exists()) {
                        userCategory.setText(user_category(document1));
                        userName.setText(document1.getData().get("name").toString());
                        userPhone.setText(document1.getData().get("phoneNumber").toString());
                        userBirthDay.setText(document1.getData().get("birthDay").toString());
                        userAddress.setText(document1.getData().get("address").toString());

                    }
                }else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(UserInfoView.this).load(uri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });



        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
//        Glide.with(this)
//                .load(riversRef)
//                .into(profileImage);


    }
    public void myStartActivity(Class c){
        Intent intent = new Intent(UserInfoView.this, c);
        startActivity(intent);
    }
    public String user_category(DocumentSnapshot document) {
        String category = null;
        String cat = document.getData().get("name").toString();
        if(cat.equals("sports_category")) {
            category = "운동/스포츠";
        }else if(cat.equals("tour_category")) {
            category = "여행";
        }else if(cat.equals("festival_category")) {

            category = "문화/공연";

        }else if(cat.equals("music_category")) {

            category = "음악/악기";
        }

        return category;
    }
}
