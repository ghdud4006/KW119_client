package com.example.task.kw119;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private static final String TAG="KW119-Topic";
    private static final String GET_TOPIC_URL_ADDRESS="http://13.125.217.245:3000/topic";
    private static final String GET_IMAGE_URL_ADDRESS="http://13.125.217.245:3000/static/";

    // client code var
    private String mResponseMsg;
    private int mUserSessionId;
    private int mTopicNum;

    // topic data
    private String mTitle, mKind, mLocation, mDate, mContents, mResult, mImgName;
    private Bitmap mBitmap;


    // widget var
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

        // 액션바 뒤로가기 버튼 추가
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mRequestTopicData();
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
        mTvTitle = (TextView)findViewById(R.id.tvTopicTitle);
        mTvKind = (TextView)findViewById(R.id.tvTopicKind);
        mTvLocation = (TextView)findViewById(R.id.tvTopicLocation);
        mTvDate = (TextView)findViewById(R.id.tvTopicDate);
        mTvContents = (TextView)findViewById(R.id.tvTopicContents);
        mTvResult = (TextView)findViewById(R.id.tvTopicResult);
        mIvAddedImg = (ImageView)findViewById(R.id.ivTopicImg);
        mBtnUpdate = (Button)findViewById(R.id.btnTopicUpdate);
        mBtnDelete = (Button)findViewById(R.id.btnTopicDelete);
        mBtnCancel = (Button)findViewById(R.id.btnTopicCancel);
    }

    // 데이터 초기화 //
    private void mInitVariables(){
        mTopicNum = 1;
        mKind = null;
        mLocation = null;
        mDate = null;
        mContents = null;
        mResult = null;
    }

    /**
     * button listener
     */
    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.btnTopicUpdate:
                mOnClickBtnUpdate();
                break;
            case R.id.btnTopicDelete:
                mOnClickBtnDelete();
                break;

            case R.id.btnTopicCancel:
                mOnClickBtnCancel();
                break;

        }
    }

    private void mOnClickBtnUpdate(){
        return;
    }

    private void mOnClickBtnDelete(){
        return;
    }

    private void mOnClickBtnCancel(){
        finish();
        return;
    }




    /**
     * client code
     */
    private void mRequestTopicData() {
        // request image
        GetTopicImage getTopicImage = new GetTopicImage();
        getTopicImage.execute(GET_IMAGE_URL_ADDRESS+Integer.toString(mUserSessionId)+"/"+mImgName);
        // request json data
        GetTopicData getTopicData = new GetTopicData();
        getTopicData.execute(GET_TOPIC_URL_ADDRESS);
    }

    /**
     * http thread
     * request data
     */
    private class GetTopicData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            mResponseMsg=null;
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
                    mResponseMsg = buffer.toString();
                    Log.v(TAG, "receive data from server");
                    return mResponseMsg;
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


        /**
         * http thread
         * request image
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(mResponseMsg.equals("err")){ // internal server error
                Toast.makeText(getApplicationContext(), mResponseMsg, Toast.LENGTH_SHORT).show();
                finish();
            } else { // set topic info to view

            }
        }

    }

    private class GetTopicImage extends AsyncTask<String, String, Bitmap> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TopicActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.show();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                mBitmap = BitmapFactory
                        .decodeStream((InputStream) new URL(args[0])
                                .getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return mBitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                mIvAddedImg.setImageBitmap(image);
                pDialog.dismiss();
            } else {
                pDialog.dismiss();
            }
        }
    }
}
