package com.example.task.kw119;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;

import com.example.task.kw119.Modules.ListViewAdaptor.MyListViewAdaptor;
import com.example.task.kw119.Modules.ListViewAdaptor.ListView_item;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * MyList Activity
 * ==============================
 * show each user's topic list
 * ==============================
 * made by Lee Ho Young (ghdud4006@gmail.com)
 */
public class MyListActivity extends AppCompatActivity {

    private static final String TAG="CAMPUS119-MyList";
    private static final String MY_LIST_URL_ADDRESS="http://13.125.217.245:3000/mylist";

    private String mResponseMsg;
    private int mUserSessionId, mTopicNum;

    //widget
    private Spinner mSpinKind, mSpinLocation;
    private ListView mListView;

    //list item, adaptor
    MyListViewAdaptor myListViewAdaptor;
    ArrayList<ListView_item> itemArrayList;

    private String mKind, mWhere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        Log.v(TAG, "MyListActivity onCreate");
        setTitle("나의 신고 처리 상황");
        // set action bar button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        // get intent
        Intent intent = getIntent();
        mUserSessionId = intent.getExtras().getInt("sid");

        // initialize var
        mInitComponents();
        mInitVariables();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("sid", mUserSessionId);
                intent.putExtra("topic_num", itemArrayList.get(position).getTopicId());
                intent.putExtra("img_path", itemArrayList.get(position).getImgPath());
                startActivity(intent);
            }
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        // require data to server
        //myListViewAdaptor.notifyDataSetChanged();
        GetTopicList getTopicList = new GetTopicList();
        getTopicList.execute(MY_LIST_URL_ADDRESS);
    }

    /**
     * initializer
     */
    // 위젯 초기화 //
    private void mInitComponents(){
        mSpinKind = (Spinner)findViewById(R.id.spinMyKind);
        mSpinLocation = (Spinner)findViewById(R.id.spinMyLocation);
        mListView = (ListView)findViewById(R.id.listMy);

        itemArrayList = new ArrayList<ListView_item>();
    }

    // 데이터 초기화 //
    private void mInitVariables(){
        mResponseMsg = null;
        mKind = null;
        mWhere = null;
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
     *  HTTP Thread
     *  res : sid
     *  req : json array (user's history data)
     *  1. json object를 담고 있는 json array를 요청
     *  2. json array -> json object 로 parsing
     *  3. json object를 itemArrayList 에 추가, 리스트뷰에 이를 추가
     *
     */
    public class GetTopicList extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            itemArrayList.clear();
            mResponseMsg=null;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("sid", mUserSessionId);

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
                Log.v(TAG, "server responses:"+mResponseMsg);

                JsonParser jsonParser = new JsonParser();
                Object resObject = jsonParser.parse(mResponseMsg);
                JsonArray jsonArray = (JsonArray)resObject;

                for(int i=0; i<jsonArray.size(); i++){
                    JsonObject jsonObj = (JsonObject)jsonArray.get(i);
                    String title = jsonObj.get("title").toString();
                    String author = jsonObj.get("author").toString();
                    String date = jsonObj.get("date").toString();
                    String kind= jsonObj.get("kind").toString();
                    String location= jsonObj.get("location").toString();
                    String image_path= jsonObj.get("image_path").toString();

                    title = title.substring(1,title.length()-1);
                    author = author.substring(1,author.length()-1);
                    date = date.substring(1,11);
                    kind = kind.substring(1,kind.length()-1);
                    location = location.substring(1,location.length()-1);
                    image_path = image_path.substring(1,image_path.length()-1);

                    itemArrayList.add(new ListView_item(
                            Integer.parseInt(jsonObj.get("topic_num").toString()),
                            title, author, date, kind, location, image_path));

                }
                myListViewAdaptor = new MyListViewAdaptor(MyListActivity.this, itemArrayList);
                mListView.setAdapter(myListViewAdaptor);
            }
        }

    }
}
