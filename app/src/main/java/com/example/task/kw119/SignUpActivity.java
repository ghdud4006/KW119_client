package com.example.task.kw119;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
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
 * SignUp Activity
 * ==============================
 * sign up function
 *
 * ==============================
 * made by Lee Ho Young (ghdud4006@gmail.com)
 */
public class SignUpActivity extends AppCompatActivity {

    private static final String TAG="CAMPUS119-Home-SignUp";
    private static final String SIGN_UP_URL_ADDRESS="http://13.125.217.245:3000/signup";

    // data
    private String mUserId, mUserPwd1, mUserPwd2, mUserStuId, mUserName;
    private boolean mUserIsStudent;

    // widgets
    private EditText mEditUserId, mEditUserPwd1, mEditUserPwd2, mEditUserStuId, mEditUserName;

    private RadioGroup mGroupIdentity;

    // system msg
    private String mResponseMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Log.v(TAG, "SignUp Activity onCreate");

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mInitComponents();
        mInitVariables();
    }

    private void mInitComponents(){
        mEditUserId = (EditText)findViewById(R.id.editSignUpId);
        mEditUserPwd1 = (EditText)findViewById(R.id.editSignUpPwd);
        mEditUserPwd2 = (EditText)findViewById(R.id.editSignUpPwd2);
        mEditUserStuId = (EditText)findViewById(R.id.editSignUpStuId);
        mEditUserName = (EditText)findViewById(R.id.editSignUpName);
        mGroupIdentity = (RadioGroup)findViewById(R.id.groupIdentity);
        mGroupIdentity.check(R.id.radioStudent);
    }
    private void mInitVariables(){
        mUserId = null;
        mUserPwd1 = null;
        mUserPwd2 = null;
        mUserStuId = null;
        mUserName = null;
        mUserIsStudent = true;
        mResponseMsg = null;
    }




    /////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// 리스너 함수  ////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.btnSignUpOk:
                mOnBtnSignUpOk();
                break;

            case R.id.btnSignUpCancel:
                mOnBtnSignUpCancel();
                break;

            case R.id.radioStudent:
                mOnRadioBtn(v.getId());
                break;

            case R.id.radioStaff:
                mOnRadioBtn(v.getId());
                break;

        }
    }

    // 디바이스 뒤로가기 버튼 //
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// 이벤트 처리 함수 ////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////


    // 회원가입 버튼 //
    private void mOnBtnSignUpOk(){
        //변수 초기화
        mInitVariables();
        //EditText 값 변수 저장
        mUserId = mEditUserId.getText().toString();
        mUserPwd1 = mEditUserPwd1.getText().toString();
        mUserPwd2 = mEditUserPwd2.getText().toString();
        mUserName = mEditUserName.getText().toString();
        mUserStuId = mEditUserStuId.getText().toString();

        //필수 입력 예외 처리
        if(mUserId.equals("") || mUserPwd1.equals("") || mUserPwd2.equals("") || mUserName.equals("")
                || mUserId==null || mUserPwd1==null || mUserPwd2==null || mUserName==null){
            Toast.makeText(getApplicationContext(), "필수사항을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        //1차 2차 비밀번호 불일치 예외처리
        if(!mUserPwd1.equals(mUserPwd2)) {
            Toast.makeText(getApplicationContext(), "입력 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        //서버 전송
        //서버 연결 스레드 실행
        ConnServerAsyncTask connServerAsyncTask = new ConnServerAsyncTask();
        connServerAsyncTask.execute(SIGN_UP_URL_ADDRESS);
    }


    // 취소 버튼 //
    private void mOnBtnSignUpCancel(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    // 신원 분류 //
    private void mOnRadioBtn(int id){
        switch(id){
            case R.id.radioStudent:
                mUserIsStudent = true;
                break;
            case R.id.radioStaff:
                mUserIsStudent = false;
                break;
        }
    }



    /////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// 서버 연결 스레드 ////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    public class ConnServerAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            mResponseMsg=null;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("UserId", mUserId);
                jsonObject.accumulate("UserPwd", mUserPwd1);
                jsonObject.accumulate("UserStuId", mUserStuId);
                jsonObject.accumulate("UserName", mUserName);
                jsonObject.accumulate("UserIsStudent", mUserIsStudent);

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
            if(mResponseMsg.equals("ack")){
                Toast.makeText(getApplicationContext(), "회원가입 완료.\n로그인 해주세요.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            } else if (mResponseMsg.equals("nack")){
                Toast.makeText(getApplicationContext(), "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
            } else { //err
                Toast.makeText(getApplicationContext(), mResponseMsg, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
