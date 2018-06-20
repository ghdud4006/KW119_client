package com.example.task.kw119;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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
 * MyList Activity
 * ==============================
 * show each user's topic list
 *
 * ==============================
 * made by Lee Ho Young (ghdud4006@gmail.com)
 */
public class MyListActivity extends AppCompatActivity {

    private static final String TAG="KW119-MyList";
    private static final String MY_LIST_URL_ADDRESS="http://13.125.217.245:3000/mylist";

    private String mResponseMsg;
    private int mUserSessionId, mTopicNum;
    private String mTopicTitle;

    private Spinner mSpinKind, mSpinLocation;

    private String mKind, mWhere;
    private int mPreEndIndex;


    // response data


    /**
     * 임시 테스트 데이터
     */
    static final String[] testListEntries = {"test1","test2","test3","test3","test3","test3","test3","test3","test3","test3","test3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        /**
         * app setting
         */
        Log.v(TAG, "MyListActivity onCreate");

        setTitle("나의 신고 처리 상황");

        Intent intent = getIntent();
        mUserSessionId = intent.getExtras().getInt("sid");

        mInitComponents();
        mInitVariables();

        // 액션바 뒤로가기 버튼 추가
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        /**
         * 임시 테스트 코드 @@@@@@@@@@@@@@@@@@ START @@@@@@@@@@@@@@@@
         */

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, testListEntries);

        ListView listView = (ListView)findViewById(R.id.listMy);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){ // 아이템 클릭 을 받음
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("sid", mUserSessionId);
                intent.putExtra("topic_num", mTopicNum);
                intent.putExtra("topic_title", mTopicTitle);
                startActivity(intent);
            }
        });
        /**
         * 임시 테스트 코드 @@@@@@@@@@@@@@@@@ END @@@@@@@@@@@@@@@@@@
         */

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
                mKind = parent.getItemAtPosition(0).toString();
            }
        });

        // 위치 정보 스피너 리스너 //
        mSpinLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mWhere = null;
                mWhere = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mWhere = null;
                mWhere = parent.getItemAtPosition(0).toString();
            }
        });
    }
    /**
     * initializer
     */
    // 위젯 초기화 //
    private void mInitComponents(){
        mSpinKind = (Spinner)findViewById(R.id.spinMyKind);
        mSpinLocation = (Spinner)findViewById(R.id.spinMyLocation);
    }

    // 데이터 초기화 //
    private void mInitVariables(){
        mResponseMsg = null;
        mKind = null;
        mWhere = null;
        mTopicTitle = null;
        mPreEndIndex = 0;
        mTopicNum = 0;

    }

    /**
     * button listener
     */
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
     * client code (http json asyncTask)
     */
    public class GetTopicList extends AsyncTask<String, String, String> {

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
                //jsonObject.accumulate("kind", mKind);
                //jsonObject.accumulate("location", mWhere);
                jsonObject.accumulate("preEndIndex", mPreEndIndex);

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
            if(mResponseMsg.equals("err")){
                Toast.makeText(getApplicationContext(), mResponseMsg, Toast.LENGTH_SHORT).show();
                finish();
            } else { // result
                mPreEndIndex = 1;

            }
        }

    }
}
