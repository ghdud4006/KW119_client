package com.example.task.kw119;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class TopicActivity extends AppCompatActivity {

    private static final String TAG="KW119-MyList";
    private static final String GET_TOPIC_URL_ADDRESS="http://13.125.217.245:3000/topic";

    private String mServerMsg;
    private int mUserSessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        setTitle("타이틀");

        Intent intent = getIntent();
        mUserSessionId = intent.getExtras().getInt("sid");

        // 액션바 뒤로가기 버튼 추가
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