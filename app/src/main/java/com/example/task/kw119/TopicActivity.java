package com.example.task.kw119;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
 * Topic Activity (MyTopic)
 * ==============================
 * show selected topic
 * user gets topic information from server
 * user can Revise or Delete topic
 * ==============================
 * made by Lee Ho Young (ghdud4006@gmail.com)
 */
public class TopicActivity extends AppCompatActivity {

    private static final String TAG="KW119-MyList";
    private static final String GET_TOPIC_URL_ADDRESS="http://13.125.217.245:3000/topic";


    // use for server comm
    private String mServerMsg;
    private int mUserSessionId;
    private int mTopicNum;


    private String mTitle, mKind, mLocation, mDate, mContents, mResult;



    private TextView mTvTitle, mTvKind, mTvLocation, mTvDate, mTvContents, mTvResult;
    private ImageView mIvAddedImg;
    private Button mBtnUpdate, mBtnDelete, mBtnCancel;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        // initiallize activity
        Intent intent = getIntent();
        mUserSessionId = intent.getExtras().getInt("sid");
        mTopicNum = intent.getExtras().getInt("topic_num");
        mTitle = intent.getStringExtra("topic_title");

        setTitle(mTitle);

        mInitComponents();
        mInitVariables();
        // get topic information
        GetTopicAsyncTask submitJsonAsyncTask = new GetTopicAsyncTask();
        submitJsonAsyncTask.execute(GET_TOPIC_URL_ADDRESS);
        ///////////////////////



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

    /**
     * initializer
     */


    // 위젯 초기화 //
    private void mInitComponents(){

    }

    // 데이터 초기화 //
    private void mInitVariables(){

    }


    private void mOnClick(View v){
        switch (v.getId()){
            case R.id.btnTopicUpdate:

                break;
            case R.id.btnTopicCancel:
                finish();
                break;

        }
    }



    /**
     * client code
     */
    public class GetTopicAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            mServerMsg=null;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("sid", mUserSessionId);
                jsonObject.accumulate("topicNumber", mTopicNum);


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
                    mServerMsg = buffer.toString();
                    Log.v(TAG, "receive data from server");
                    return mServerMsg;
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
            if(mServerMsg.equals("err")){ // internal server error
                Toast.makeText(getApplicationContext(), mServerMsg, Toast.LENGTH_SHORT).show();
                finish();
            } else { // get topic info

            }
        }

    }
}
