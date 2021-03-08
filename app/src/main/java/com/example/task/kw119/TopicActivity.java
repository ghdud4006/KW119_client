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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
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

    private static final String TAG="CAMPUS119-Topic";

    private static final String GET_TOPIC_URL_ADDRESS="http://13.125.217.245:3000/topic";
    private static final String GET_IMAGE_URL_ADDRESS="http://13.125.217.245:3000/static/";
    private static final String DELETE_TOPIC_URL_ADDRESS="http://13.125.217.245:3000/delete";

    // client code var
    private String mResponseMsg;
    private int mUserSessionId;
    private int mTopicNum;

    // topic data
    private String mTitle, mKind, mLocation, mDate, mContents, mResult, mImgPath;
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
        mImgPath = intent.getStringExtra("img_path");

        setTitle(Integer.toString(mTopicNum));

        mInitComponents();
        mInitVariables();

        // 액션바 뒤로가기 버튼 추가
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRequestTopicData();
    }

    // 디바이스 뒤로가기 버튼 //
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "수정");
        menu.add(Menu.NONE, 2, Menu.NONE, "삭제");
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 액션바 뒤로가기 버튼
                finish();
                return true;
            case 1: //메뉴 1 '수정'
                mOnClickBtnUpdate();
                break;
            case 2: //메뉴 2 '삭제'
                mOnClickBtnDelete();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    }

    // 데이터 초기화 //
    private void mInitVariables(){
        mKind = null;
        mLocation = null;
        mDate = null;
        mContents = null;
        mResult = null;
    }



    /**
     * button listener
     */

    private void mOnClickBtnUpdate(){
        Intent intent = new Intent(getApplicationContext(), TopicUpdateActivity.class);
        intent.putExtra("topic_num", mTopicNum);
        intent.putExtra("title", mTitle);
        intent.putExtra("content", mContents);
        startActivity(intent);
        finish();
    }

    private void mOnClickBtnDelete(){
        DeleteTopicData deleteTopicData = new DeleteTopicData();
        deleteTopicData.execute(DELETE_TOPIC_URL_ADDRESS);
    }




    /**
     * client code
     * http
     */
    private void mRequestTopicData() {
        // request image
        GetTopicImage getTopicImage = new GetTopicImage();
        getTopicImage.execute(GET_IMAGE_URL_ADDRESS+Integer.toString(mUserSessionId)+"/"+mImgPath);
        // request json data
        GetTopicData getTopicData = new GetTopicData();
        getTopicData.execute(GET_TOPIC_URL_ADDRESS);
    }

    /**
     * request data thread
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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(mResponseMsg.equals("err")){ // internal server error
                Toast.makeText(getApplicationContext(), mResponseMsg, Toast.LENGTH_SHORT).show();
                finish();
            } else { // set topic info to view
                // parsing String to json
                Log.v(TAG,"SERVER RESPONSE MSG : "+mResponseMsg);
                JsonParser jsonParser = new JsonParser();
                Object object = jsonParser.parse(mResponseMsg);
                try {
                    JsonObject jsonObject = (JsonObject)object;
                    mTitle = jsonObject.get("title").toString();
                    mDate = jsonObject.get("date").toString();
                    mKind = jsonObject.get("kind").toString();
                    mLocation = jsonObject.get("location").toString();
                    mContents = jsonObject.get("contents").toString();

                    mTitle = mTitle.substring(1,mTitle.length()-1);
                    mDate = mDate.substring(1,11);
                    mKind = mKind.substring(1,mKind.length()-1);
                    mLocation = mLocation.substring(1,mLocation.length()-1);
                    mContents = mContents.substring(1,mContents.length()-1);

                    mTvTitle.setText(mTitle);
                    mTvKind.setText(mKind);
                    mTvLocation.setText(mLocation);
                    mTvDate.setText(mDate);
                    mTvContents.setText(mContents);
                    //mTvResult.setText("");
                } catch (JsonIOException e) {
                    e.printStackTrace();
                }
            }
        }

    }



    /**
     * request image thread
     */
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


    private class DeleteTopicData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            mResponseMsg=null;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("topic_num", mTopicNum);

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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(mResponseMsg.equals("err")){ // internal server error
                Toast.makeText(getApplicationContext(), mResponseMsg, Toast.LENGTH_LONG).show();
                finish();
            } else { // set topic info to view
                Toast.makeText(getApplicationContext(), "삭제 완료.", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }
}
