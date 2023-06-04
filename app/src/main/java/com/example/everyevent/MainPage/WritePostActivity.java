package com.example.everyevent.MainPage;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.everyevent.BasicActivity;
import com.example.everyevent.HomeEventList.PostInfo;
import com.example.everyevent.Login.MemberInfo;
import com.example.everyevent.Login.MemberInitActivity;
import com.example.everyevent.R;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class WritePostActivity extends BasicActivity {
    TextView dateText;
    Button datePickerBtn;

    private static final String TAG = "WritePostActivity";
    FirebaseUser user;
    private Uri uri;

    private String category;

    private ImageView mImage;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_GALLERY = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        findViewById(R.id.check).setOnClickListener(onClickListener);
         dateText = (TextView) findViewById(R.id.date_text_view);
         datePickerBtn = findViewById(R.id.date_pick_btn);
         dateText.setOnClickListener(onClickListener);
         datePickerBtn.setOnClickListener(onClickListener);
         mImage = findViewById(R.id.imageView);
         findViewById(R.id.createImage).setOnClickListener(onClickListener);
        findViewById(R.id.sports_category1).setOnClickListener(onClickListener);
        findViewById(R.id.sports_category2).setOnClickListener(onClickListener);
        findViewById(R.id.tour_category1).setOnClickListener(onClickListener);
        findViewById(R.id.tour_category2).setOnClickListener(onClickListener);
        findViewById(R.id.festival_category1).setOnClickListener(onClickListener);
        findViewById(R.id.festival_category2).setOnClickListener(onClickListener);
        findViewById(R.id.music_category1).setOnClickListener(onClickListener);
        findViewById(R.id.music_category2).setOnClickListener(onClickListener);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.check:
                    try {
                        profileUpdate();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case R.id.date_pick_btn:
                {
                    Calendar calendar = Calendar.getInstance();
                    int pYear = calendar.get(Calendar.YEAR);
                    int pMonth = calendar.get(Calendar.MONTH);
                    int pDay = calendar.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(WritePostActivity.this,
                    new DatePickerDialog.OnDateSetListener(){
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month,int day ){

                            month = month +1;
                            String date = year + "/" +month + "/" + day;

                            dateText.setText(date);
                        }
                    }, pYear, pMonth, pDay);
                    datePickerDialog.show();
                    break;
                }
                case R.id.createImage: {
                    Log.e("실행","실행");
                    Intent intent = new Intent(getApplicationContext(), WritePostPopUp.class);
                    startActivityForResult(intent, 1);
                    break;
                }
                case R.id.sports_category1:
                case R.id.sports_category2:
                    findViewById(R.id.sports_cat).setBackgroundColor(Color.parseColor("#D3D3D3"));
                    findViewById(R.id.tour_cat).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    findViewById(R.id.festival_cat).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    findViewById(R.id.music_cat).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    category ="sports_category";
                    break;

                case R.id.tour_category1:
                case R.id.tour_category2:
                    findViewById(R.id.sports_cat).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    findViewById(R.id.tour_cat).setBackgroundColor(Color.parseColor("#D3D3D3"));
                    findViewById(R.id.festival_cat).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    findViewById(R.id.music_cat).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    category ="tour_category";
                    break;

                case R.id.festival_category1:
                case R.id.festival_category2:
                    findViewById(R.id.sports_cat).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    findViewById(R.id.tour_cat).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    findViewById(R.id.festival_cat).setBackgroundColor(Color.parseColor("#D3D3D3"));
                    findViewById(R.id.music_cat).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    category ="festival_category";
                    break;

                case R.id.music_category1:
                case R.id.music_category2:
                    findViewById(R.id.sports_cat).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    findViewById(R.id.tour_cat).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    findViewById(R.id.festival_cat).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    findViewById(R.id.music_cat).setBackgroundColor(Color.parseColor("#D3D3D3"));
                    category ="music_category";
                    break;

            }
        }
    };

    private void profileUpdate() throws ParseException {
        user = FirebaseAuth.getInstance().getCurrentUser();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        final String cat = category;
        String userId = user.getUid();
        final String title = ((EditText)findViewById(R.id.titleEditText)).getText().toString();
        final String contents = ((EditText)findViewById(R.id.contentsEditText)).getText().toString();
        final String address = ((EditText)findViewById(R.id.addressEditText)).getText().toString();
        Date startDate = null;
        try{
            startDate = dateFormat.parse((dateText.getText().toString()));
        }catch (Exception e){}
        final String numberOfPeopleCanApply = ((EditText)findViewById(R.id.numberOfPeopleCanApplyEditText)).getText().toString();
        if(title.length() > 0 && contents.length() > 0 && address.length() >0  && numberOfPeopleCanApply.length() >0 ) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference documentReference = db.collection("posts").document();
            PostInfo postInfo = new PostInfo(userId,cat,title, contents,address,startDate,numberOfPeopleCanApply,documentReference.getId());
            uploader(postInfo, documentReference);
            checkUserGeneratedEvents(documentReference.getId());
            imageUpload(documentReference.getId());

            startToast("이벤트 생성을 완료하였습니다");
            finish();
            Intent intent = new Intent(WritePostActivity.this, MainActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else{
            startToast("이벤트를 작성해 주세요");

        }

    }

    private void uploader(PostInfo postInfo,DocumentReference documentReference) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        documentReference.set(postInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                finish();
            }
        });
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }
    private void checkUserGeneratedEvents(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference washingtonRef = db.collection("users").document("user_id_"+user.getUid());
        washingtonRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> generatedEvents = (ArrayList<String>) document.getData().get("generatedEvents");
                        if(generatedEvents != null) {
                                generatedEvents.add(documentId);

                        }else{
                            generatedEvents = new ArrayList<String>();
                            generatedEvents.add(documentId);
                        }
                        washingtonRef
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

    public void goGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    //카메라 intent
    public void goCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
        }
        // 사진을 저장하고 이미지뷰에 출력
        if (photoFile != null) {
            uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.everyevent.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        // 파일이름을 세팅 및 저장경로 세팅
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    private void imageUpload(String documentId) {



            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child("images/"+documentId+"/eventImage.jpg");
            UploadTask uploadTask = riversRef.putFile(uri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return riversRef.getDownloadUrl();
                }
            });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(WritePostActivity.this, MainActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data.getIntExtra("result", 0) == 0) {

                    goGallery();

                } else {
                    if (ActivityCompat.checkSelfPermission(WritePostActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        goCamera();
                    } else {
                        ActivityCompat.requestPermissions(WritePostActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
                    }

                }

            }
        } else {
            switch (requestCode) {
                case (REQUEST_GALLERY):
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            uri = data.getData();
                            Log.e("mI", uri.toString());
                            Glide.with(this).load(uri).centerCrop().override(300).into(mImage);
                        } catch (Exception e) {
                        }
                        ;
                    }
                    break;
                case (REQUEST_CAMERA):
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            Glide.with(this).load(uri).centerCrop().override(300).into(mImage);
                        } catch (Exception e) {
                        }
                        ;
                    }
                    break;
            }
        }
    }
    }




