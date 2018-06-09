package com.example.task.kw119;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.task.kw119.Modules.SaveSharedPreference;

/**
 * Root Activity
 * ==============================
 *
 *
 * ==============================
 * made by Lee Ho Young (ghdud4006@gmail.com)
 */
public class RootActivity extends AppCompatActivity {

    private static final String TAG="KW119-Root";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        Log.v(TAG, "rootActivity onCreate");

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // 자동 로그인 //
                if(SaveSharedPreference.getUserName(RootActivity.this).length() == 0)
                {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    Log.v(TAG, "move to login act");
                    finishAffinity();
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "자동 로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "move to home act");
                    finishAffinity();
                }
            }
        }, 1500);// 1.5초 정도 딜레이를 준 후 시작
    }

}
