package com.example.everyevent.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.example.everyevent.BasicActivity;
import com.example.everyevent.MainPage.MainActivity;
import com.example.everyevent.MainPage.WritePostActivity;
import com.example.everyevent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BasicActivity {


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.updateButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoPasswordResetButton).setOnClickListener(onClickListener);
    }


        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.updateButton:
                        login();
                        break;
                    case R.id.gotoPasswordResetButton:
                        myStartActivity(PasswordResetActivity.class);
                        break;
                }

            }
        };

    private void login() {
        String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString();

        if(email.length()>0 && password.length()>0) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인에 성공하였습니다");
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            } else {
                                if(task.getException()!=null) {


                                    // If sign in fails, display a message to the user.
                                    startToast(task.getException().toString());
                                    //UI

                                }
                            }
                        }
                    });



        }else{
            startToast("이메일 또는 비밀번호를 입력해 주세요");

        }


    }

    private void startToast(String msg) {
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
