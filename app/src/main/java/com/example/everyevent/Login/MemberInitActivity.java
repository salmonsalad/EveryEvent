package com.example.everyevent.Login;


import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;


import com.bumptech.glide.Glide;
import com.example.everyevent.BasicActivity;
import com.example.everyevent.MainPage.MainActivity;
import com.example.everyevent.MainPage.WritePostActivity;
import com.example.everyevent.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MemberInitActivity extends BasicActivity {
    private static final String TAG = "MemberInitActivity";
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 0;
    private Uri uri;
    private ImageView mImage;
    private Button mGallery, mCamera;
    private String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        findViewById(R.id.updateButton).setOnClickListener(onClickListener);
        mImage = findViewById(R.id.imageView);
        mImage.setOnClickListener(onClickListener);

        mGallery = findViewById(R.id.btn_gallery);
        mGallery.setOnClickListener(onClickListener);
        mCamera = findViewById(R.id.btn_picture);
        mCamera.setOnClickListener(onClickListener);
        findViewById(R.id.sports_category1).setOnClickListener(onClickListener);
        findViewById(R.id.sports_category2).setOnClickListener(onClickListener);
        findViewById(R.id.tour_category1).setOnClickListener(onClickListener);
        findViewById(R.id.tour_category2).setOnClickListener(onClickListener);
        findViewById(R.id.festival_category1).setOnClickListener(onClickListener);
        findViewById(R.id.festival_category2).setOnClickListener(onClickListener);
        findViewById(R.id.music_category1).setOnClickListener(onClickListener);
        findViewById(R.id.music_category2).setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed()    {
       // super.onBackPressed();
        startToast("회원정보를 입력하세요");
    }

        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.updateButton:
                        profileUpdate();
                        break;
                    case R.id.imageView:
                        CardView cardView = findViewById(R.id.buttons_card_view);
                        if(cardView.getVisibility() == View.VISIBLE){
                            cardView.setVisibility(View.GONE);
                        }
                        else{
                            cardView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case R.id.btn_gallery:
                        goGallery();
                        break;
                    case R.id.btn_picture:
                        Log.e("tga","adga");
                        if (ActivityCompat.checkSelfPermission(MemberInitActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            goCamera();
                        }else{
                            ActivityCompat.requestPermissions(MemberInitActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
                        }
                        break;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case (REQUEST_GALLERY):
                if(resultCode == Activity.RESULT_OK) {
                    try {
                        uri = data.getData();
                        Log.e("mI",uri.toString());
                        Glide.with(this).load(uri).centerCrop().override(300).into(mImage);
                    }catch(Exception e){};
                }
                break;
            case (REQUEST_CAMERA):
                if(resultCode == Activity.RESULT_OK) {
                    try {
                        Glide.with(this).load(uri).centerCrop().override(300).into(mImage);
                    }catch(Exception e){};
                }
                break;
        }
    }

    //갤러리 intent
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

    private void profileUpdate() {
        String name = ((EditText)findViewById(R.id.nameEditText)).getText().toString();
        String phoneNumber = ((EditText)findViewById(R.id.phoneNumberEditText)).getText().toString();
        String birthDay = ((EditText)findViewById(R.id.birthDayEditText)).getText().toString();
        String address = ((EditText)findViewById(R.id.addressEditText)).getText().toString();

        Log.e("error", name + " " + phoneNumber +" " + birthDay + " " + address);
        if(name.length()>0 && phoneNumber.length()>9 && birthDay.length()>5 && address.length() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child("images/" + user.getUid() + "/profileImage.jpg");
            UploadTask uploadTask = riversRef.putFile(uri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Log.d("tt","tt");
                        throw Objects.requireNonNull(task.getException());
                    }
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        MemberInfo memberInfo = new MemberInfo(category,name, phoneNumber, birthDay, address);
                        db.collection("users").document("user_id_" + user.getUid()).set(memberInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startToast("회원정보 등록을 성공하였습니다.");
                                        finish();
                                        Intent intent = new Intent(MemberInitActivity.this, MainActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        startToast("회원정보 등록에 실패하였습니다.");
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });

                    } else {
                        startToast("회원 정보를 입력해주세요");
                    }
                }
            });
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }
}
