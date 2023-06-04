package com.example.everyevent.MainPage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.everyevent.R;

public class WritePostPopUp extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.writepost_popup);

        findViewById(R.id.storageimage).setOnClickListener(onClickListener);
        findViewById(R.id.runcamera).setOnClickListener(onClickListener);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.storageimage: {
                    Intent intent = new Intent();
                    intent.putExtra("result", 0);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                }
                case R.id.runcamera: {
                    Intent intent = new Intent();
                    intent.putExtra("result", 1);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                }

            }

        }
    };



//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //바깥레이어 클릭시 안닫히게
//        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
//            return false;
//        }
//        return true;
//    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}


