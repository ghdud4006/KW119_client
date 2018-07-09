package com.example.task.kw119;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.task.kw119.Modules.NetworkService;
import com.example.task.kw119.model.UploadResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TopicUpdateActivity extends AppCompatActivity {

    private static final String TAG="KW119-TopicUpdate";

    private static final String UPDATE_TOPIC_URL_ADDRESS="http://13.125.217.245:3000/update";

    // widget var
    private EditText mEditTitle, mEditContents, mEditWhereDetail;
    private Spinner mSpinKind, mSpinWhere;

    // data var
    private String mTitle, mKind, mContents, mWhere, mWhereDetail, mWhereTotal;
    private boolean mWhereOn;
    private int mTopicNum;
    private int mKindId, mWhereId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_update);

        Log.v(TAG, "TopicUpdateActivity onCreate");

        mInitComponents();
        mInitVariables();

        setTitle("신고 수정");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        Intent intent = getIntent();
        mTopicNum = intent.getExtras().getInt("topic_num");
        mTitle = intent.getStringExtra("title");
        mContents = intent.getStringExtra("content");

        mEditTitle.setText(mTitle);
        mEditContents.setText(mContents);

        /**
         * spinner listener
         */
        // 종류 스피너 리스너 //
        mSpinKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mKind = null;
                mKind = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mKind = null;
                mKind = parent.getItemAtPosition(mKindId).toString();
            }
        });

        // 위치 정보 스피너 리스너 //
        mSpinWhere.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mWhere = null;
                mWhere = parent.getItemAtPosition(position).toString();
                if (mWhere.equals(parent.getItemAtPosition(mWhereId).toString())){
                    mWhereOn = false;
                } else {
                    mWhereOn = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mWhere = null;
                mWhere = parent.getItemAtPosition(0).toString();
                mWhereOn = false;
            }
        });
    }

    // 위젯 초기화 //
    private void mInitComponents(){
        mEditTitle = (EditText)findViewById(R.id.editUpdateTitle);
        mEditContents = (EditText)findViewById(R.id.editUpdateContents);
        mEditWhereDetail = (EditText)findViewById(R.id.editUpdateDetail);
        mSpinKind = (Spinner)findViewById(R.id.spinUpdateKind);
        mSpinWhere = (Spinner)findViewById(R.id.spinUpdateWhere);
    }

    // 데이터 초기화 //
    private void mInitVariables(){
        mTitle = null;
        mKind = null;
        mContents = null;
        mWhere = null;
        mWhereDetail = null;
        mWhereTotal = null;
        mWhereOn = false;
    }



    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.btnUpdateOk:
                mOnBtnUpdate();
                break;
            case R.id.btnUpdateCancel:
                finish();
                break;

        }
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


    // 제출 버튼 //
    private void mOnBtnUpdate(){
        //데이터 초기화
        mTitle=null;
        mContents=null;
        mWhereDetail=null;
        mWhereTotal=null;

        //데이터 파싱
        mTitle = mEditTitle.getText().toString();
        mContents = mEditContents.getText().toString();
        mWhereDetail = mEditWhereDetail.getText().toString();

        if(mWhereOn==true){
            mWhereTotal = mWhere+'/'+ mWhereDetail;
        } else {
            mWhereTotal = mWhere;
        }

        //not null 예외 처리
        if(mTitle==null || mTitle.equals("")){
            Toast.makeText(getApplicationContext(), "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mKind==null || mKind.equals("")){
            Toast.makeText(getApplicationContext(), "종류를 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mContents==null || mContents.equals("")){
            Toast.makeText(getApplicationContext(), "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        ReqUpdateTopic reqUpdateTopic = new ReqUpdateTopic();
        reqUpdateTopic.execute(UPDATE_TOPIC_URL_ADDRESS);
    }



    private class ReqUpdateTopic extends AsyncTask<String, String, String> {

        String responseMsg;

        @Override
        protected void onPreExecute() {
            responseMsg=null;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("topic_num",mTopicNum);
                jsonObject.accumulate("title", mTitle);
                jsonObject.accumulate("kind", mKind);
                jsonObject.accumulate("content", mContents);
                jsonObject.accumulate("where", mWhereTotal);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "text/html");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.connect();

                    OutputStream outStream = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }
                    responseMsg = buffer.toString();
                    Log.v(TAG, "receive data from server");
                    return responseMsg;
                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.v(TAG,"SERVER RESPONSE MSG : "+responseMsg);
            if(responseMsg.equals("err")){ // internal server error
                Toast.makeText(getApplicationContext(), "수정 실패", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "수정 완료", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

}
