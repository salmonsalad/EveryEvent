package com.example.everyevent.ShowPostList;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.everyevent.BasicActivity;
import com.example.everyevent.MainPage.MainActivity;
import com.example.everyevent.MainPage.WritePostActivity;
import com.example.everyevent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ShowPostActivity extends BasicActivity {
    Button applyButton;
    Button interestedButton;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference riversRef;
    StorageReference storageRef = storage.getReference();

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String documentId;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showpost);
        final TextView eventName = findViewById(R.id.eventname);
        final TextView eventContent = findViewById(R.id.eventcontent);
        final TextView eventPlace = findViewById(R.id.eventplace);
        TextView eventStartDate = findViewById(R.id.eventstartdate);
        TextView eventState = findViewById(R.id.eventstate);
        TextView eventPeople = findViewById(R.id.eventcomepeople);
        TextView numberOfEventApplicants = findViewById(R.id.numberofeventapplicants);
        TextView eventAllPeople = findViewById(R.id.eventallpeople);
        applyButton = findViewById(R.id.postbutton);
        interestedButton = findViewById(R.id.interestedEventBtn);
        ImageView eventImage = findViewById(R.id.showEventImage);



        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apply();
            }
        });
        interestedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    interested();
            }
        });

        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");

        riversRef = storageRef.child("images/"+documentId+"/eventImage.jpg");

        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                Glide.with(ShowPostActivity.this).load(uri).into(eventImage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        checkWhoMadeAndInterested();
        Log.e("documentId",documentId);
        DocumentReference docRef = firebaseFirestore.document("posts/"+documentId);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        eventName.setText(document.getData().get("title").toString());
                        eventContent.setText(document.getData().get("contents").toString());
                        eventPlace.setText(document.getData().get("address").toString());

                        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
                        String date = format.format(document.getDate("startDate"));
                        eventStartDate.setText(date);

                        if(document.getData().get("running").equals(true)){
                            eventState.setText("실행중");
                            eventPeople.setText("출석한 인원/수용 가능 인원");
                            numberOfEventApplicants.setText(document.getData().get("numberOfAttendees").toString());
                            eventAllPeople.setText(document.getData().get("numberOfPeopleCanApply").toString());

                        }
                        else{
                            eventAllPeople.setText(document.getData().get("numberOfPeopleCanApply").toString());
                            eventState.setText("준비중");
                            numberOfEventApplicants.setText(document.getData().get("numberOfApplicants").toString());
                            eventAllPeople.setText(document.getData().get("numberOfPeopleCanApply").toString());

                        }
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



    }

    private void interested() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userPath = firebaseFirestore.collection("users").document("user_id_"+user.getUid());

        if(interestedButton.getText() =="관심 이벤트 취소" )
        {
            userPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<String> interestedEvents = (ArrayList<String>) document.getData().get("interestedEvents");
                                interestedEvents.remove(documentId);

                            userPath
                                    .update("interestedEvents",interestedEvents)
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
            Intent intent = new Intent(ShowPostActivity.this, MainActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            startToast("관심 이벤트 등록이 취소되었습니다");
            finish();
        }else{
            userPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<String> interestedEvents = (ArrayList<String>) document.getData().get("interestedEvents");
                            if(interestedEvents != null) {
                                interestedEvents.add(documentId);

                            }else{
                                interestedEvents = new ArrayList<String>();
                                interestedEvents.add(documentId);
                            }
                            userPath
                                    .update("interestedEvents",interestedEvents)
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

        }
        Intent intent = new Intent(ShowPostActivity.this, MainActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        startToast("관심 이벤트로 등록되었습니다");
        finish();

    }

    private void checkWhoMadeAndInterested() {
        DocumentReference washingtonRef = firebaseFirestore.collection("posts").document(documentId);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        washingtonRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String Id = document.getData().get("userInfo").toString();
                        ArrayList<String> applys = (ArrayList<String>) document.getData().get("apply");
                        String nowUserId = user.getUid();
                        Log.e("userId",user.getUid());
                        if(Id.equals(nowUserId))
                        {

                            //startToast("사용자가 생성한 이벤트 입니다");
                            applyButton.setText("이벤트 삭제");

                        }else {
                            if(applys != null){
                                if (applys.contains(nowUserId)) {
                                    applyButton.setText("이벤트 신청 취소");


                                } else {
                                    applyButton.setText("이벤트 신청");


                                }
                            }else{
                                applyButton.setText("이벤트 신청");
                            }


                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        DocumentReference userPath = firebaseFirestore.collection("users").document("user_id_"+user.getUid());
        userPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> interestedEvents = (ArrayList<String>) document.getData().get("interestedEvents");
                        if(interestedEvents != null) {
                            if (interestedEvents.contains(documentId)) {
                                interestedButton.setText("관심 이벤트 취소");

                            } else {
                                interestedButton.setText("관심 이벤트 등록");
                            }
                        }else{
                            interestedButton.setText("관심 이벤트 등록");
                        }

                    }
                }
            }
        });
    }
    private void apply() {
        DocumentReference washingtonRef = firebaseFirestore.collection("posts").document(documentId);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if(applyButton.getText().equals("이벤트 삭제"))
        {

                firebaseFirestore.collection("posts").document(documentId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DocumentReference userPath = firebaseFirestore.collection("users/user_id_"+user.getUid()+"/profileInformation").document(user.getUid());
                                userPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                ArrayList<String> generatedEvents = (ArrayList<String>) document.getData().get("generatedEvents");

                                                if(generatedEvents!=null) {
                                                    generatedEvents.remove(documentId);
                                                }

                                                userPath
                                                        .update("generatedEvents",generatedEvents)
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

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

        }else if (applyButton.getText().equals("이벤트 신청 취소")){
            washingtonRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<String> applys = (ArrayList<String>) document.getData().get("apply");
                            applys.remove(user.getUid());
                            washingtonRef
                                    .update("apply", applys)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            DocumentReference userPath = firebaseFirestore.collection("users").document("user_id_" + user.getUid());

                                            userPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            ArrayList<String> applyedEvents = (ArrayList<String>) document.getData().get("applyedEvents");
                                                            applyedEvents.remove(documentId);

                                                            userPath
                                                                    .update("applyedEvents", applyedEvents)
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
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");

                                        }




                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);
                                        }
                                    });

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
                updateApplicantsNumber("remove");
                checkUserAppliedEvent(documentId,"remove");
                Intent intent = new Intent(ShowPostActivity.this, MainActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                startToast("이벤트 신청 취소를 완료했습니다");
                finish();


            }else if (applyButton.getText().equals("이벤트 신청")){
            washingtonRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<String> applys = (ArrayList<String>) document.getData().get("apply");
                            if(applys!=null) {
                                applys.add(user.getUid());
                                washingtonRef
                                        .update("apply", applys)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                DocumentReference userPath = firebaseFirestore.collection("users").document("user_id_" + user.getUid());

                                                userPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                ArrayList<String> applyedEvents = (ArrayList<String>) document.getData().get("applyedEvents");
                                                                if(applyedEvents!=null) {
                                                                    applyedEvents.add(documentId);
                                                                }else{
                                                                     applyedEvents = new ArrayList<String>();
                                                                    applyedEvents.add(documentId);
                                                                }

                                                                userPath
                                                                        .update("applyedEvents", applyedEvents)
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



                                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                            }
                                        });
                            }else{
                                applys = new ArrayList<String>();
                                applys.add(user.getUid());
                                washingtonRef
                                        .update("apply", applys)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                DocumentReference userPath = firebaseFirestore.collection("users").document("user_id_" + user.getUid());

                                                userPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                ArrayList<String> applyedEvents = (ArrayList<String>) document.getData().get("applyedEvents");
                                                                if(applyedEvents!=null) {
                                                                    applyedEvents.add(documentId);
                                                                }else{
                                                                    applyedEvents = new ArrayList<String>();
                                                                    applyedEvents.add(documentId);
                                                                }

                                                                userPath
                                                                        .update("applyedEvents", applyedEvents)
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


                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

                updateApplicantsNumber("add");
                checkUserAppliedEvent(documentId,"add");
                startToast("이벤트 신청을 완료했습니다");
            Intent intent = new Intent(ShowPostActivity.this, MainActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
                finish();
            }

    }
    private void updateApplicantsNumber(String cal) {
        DocumentReference washingtonRef = firebaseFirestore.collection("posts").document(documentId);
        washingtonRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        long number = (long) document.getData().get("numberOfApplicants");
                        if(cal.equals("add"))
                        {
                            number = number + 1;
                        }else if(cal.equals("remove")){
                            number = number - 1;
                        }
                        washingtonRef
                                .update("numberOfApplicants", number)
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



    }
    private void checkUserAppliedEvent(String documentsId,String state) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference washingtonRef = firebaseFirestore.collection("users/user_id_"+user.getUid()+"/profileInformation").document(user.getUid());

        washingtonRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> applys = (ArrayList<String>) document.getData().get("AppliedEvents");
                        if(applys != null) {
                            if (state.equals("add")) {
                                applys.add(documentsId);

                            } else if (state.equals("remove")) {
                                applys.remove(documentsId);
                            }
                        }else{
                            applys = new ArrayList<String>();
                            applys.add(documentsId);
                        }

                        washingtonRef
                                .update("AppliedEvents",applys)
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


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ShowPostActivity.this, MainActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }


// Set the "isCapital" field of the city 'DC'

    private void startToast(String msg) {
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }

}
