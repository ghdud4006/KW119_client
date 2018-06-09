package com.example.task.kw119;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

/**
 * Notice Activity
 * ==============================
 *
 *
 * ==============================
 * made by Lee Ho Young (ghdud4006@gmail.com)
 */
public class NoticeActivity extends AppCompatActivity {

    private static final String TAG="KW119-Notice";
    private static final String TOTAL_LIST_URL_ADDRESS="http://13.125.217.245:3000/notice";

    private String mServerMsg;
    private int mUserSessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        Log.v(TAG, "NoticeActivity onCreate");

        setTitle("전체 신고 처리 상황");

        Intent intent = getIntent();
        mUserSessionId = intent.getExtras().getInt("sid");

        //액션바 뒤로가기 버튼 추가
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    // 액션바 뒤로가기 버튼 //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    // 디바이스 뒤로가기 버튼 //
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
