package com.example.task.kw119;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.task.kw119.Modules.BackPressCloseHandler;
import com.example.task.kw119.Modules.SaveSharedPreference;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Home Activity
 * ==============================
 * show function menu
 *
 * ==============================
 * made by Lee Ho Young (ghdud4006@gmail.com)
 */

public class HomeActivity extends AppCompatActivity {

    private static final String TAG="KW119-Home";

    //widgets
    private Button mBtnNoticeAct, mBtnMyAct, mBtnSubmitAct, mBtnLogout, mBtnExit;

    //modules
    private BackPressCloseHandler backPressCloseHandler;

    //session id
    private int mUserSessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.v(TAG, "HomeActivity onCreate");

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        backPressCloseHandler = new BackPressCloseHandler(this);

        mBtnNoticeAct = (Button)findViewById(R.id.btnNoticeAct);
        mBtnMyAct = (Button)findViewById(R.id.btnMyAct);
        mBtnSubmitAct = (Button)findViewById(R.id.btnSubmitAct);
        mBtnLogout = (Button)findViewById(R.id.btnLogout);
        mBtnExit = (Button)findViewById(R.id.btnExit);

        //get auth
        mUserSessionId = Integer.parseInt(SaveSharedPreference.getUserName(HomeActivity.this));

        Log.v(TAG, "auth sid:"+Integer.toString(mUserSessionId));

        //공지 이동
        mBtnNoticeAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                intent.putExtra("sid", mUserSessionId);
                startActivity(intent);
            }
        });

        //mylist
        mBtnMyAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyListActivity.class);
                intent.putExtra("sid", mUserSessionId);
                startActivity(intent);
            }
        });




        //submit 이동
        mBtnSubmitAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SubmitActivity.class);
                intent.putExtra("sid", mUserSessionId);
                startActivity(intent);
            }
        });




        //로그아웃
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSharedPreference.clearUserName(HomeActivity.this);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //종료 버튼
        mBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NavUtils.navigateUpFromSameTask(HomeActivity.this);
                finishAffinity();
                System.runFinalizersOnExit(true);
                System.exit(0);
            }
        });

    }

    // 뒤로가기버튼 리스너
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}
