package com.example.task.kw119;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task.kw119.Modules.NetworkService;
import com.example.task.kw119.Modules.SaveSharedPreference;
import com.example.task.kw119.model.UploadResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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


/**
 * Submit Activity
 * ==============================
 * submit contents to server
 * upload image file to server
 * ==============================
 * made by Lee Ho Young (ghdud4006@gmail.com)
 */
public class SubmitActivity extends AppCompatActivity {

    private static final String TAG="KW119-Submit";

    private static final String SUBMIT_URL_ADDRESS="http://13.125.217.245:3000/submit";
    private static final String UPLOAD_IMG_URL_ADDRESS="http://13.125.217.245:3000";

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    // system var
    private String mServerMsg;
    private int mUserSessionId;

    // widget var
    private EditText mEditTitle, mEditContents, mEditWhereDetail;
    private Spinner mSpinKind, mSpinWhere;
    private ImageView mImgAdded;

    // data var
    private String mTitle, mKind, mContents, mImgFileName, mWhere, mWhereDetail, mWhereTotal;
    private boolean mWhereOn, mIsImgAdded;

    // add image var
    private Uri mImgCaptureUri;
    private String mImgAbsolutePath;

    // upload image var
    private ProgressDialog mDialog;
    private int mServerResCode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        Log.v(TAG, "SubmitActivity onCreate");
        /////////////////////////////////////////////////
        // 앱 세팅 /////////////////////////////////////
        setTitle("신고 하기");

        Intent intent = getIntent();
        mUserSessionId = intent.getExtras().getInt("sid");
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mInitComponents();
        mInitVariables();
        ///////////////////////////////////////////////
        /////////////////////////////////////////////////
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
        mSpinWhere.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mWhere = null;
                mWhere = parent.getItemAtPosition(position).toString();
                if (mWhere.equals(parent.getItemAtPosition(0).toString())){
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

    /**
     * initializer
     */


    // 위젯 초기화 //
    private void mInitComponents(){
        mEditTitle = (EditText)findViewById(R.id.editSubmitTitle);
        mEditContents = (EditText)findViewById(R.id.editSubmitContents);
        mEditWhereDetail = (EditText)findViewById(R.id.editWhereDetail);
        mSpinKind = (Spinner)findViewById(R.id.spinnerTopicKind);
        mSpinWhere = (Spinner)findViewById(R.id.spinnerWhere);
        mImgAdded = (ImageView)findViewById(R.id.imgAdded);
    }

    // 데이터 초기화 //
    private void mInitVariables(){
        mTitle = null;
        mKind = null;
        mContents = null;
        mImgFileName = null;
        mWhere = null;
        mWhereDetail = null;
        mWhereTotal = null;
        mServerMsg = null;
        mWhereOn = false;
        mIsImgAdded = false;
    }

    /**
     * event listener
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


    // 버튼 클릭 리스너 //
    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.btnPhoto:
                mIsImgAdded=true;
                mImgUpdateListener();
                return;

            case R.id.btnSubmitOk:
                mOnBtnSubmit();
                return;
        }
    }


    // 제출 버튼 //
    private void mOnBtnSubmit(){
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

        // 서버 image file 전송
        if(mIsImgAdded==true){
            mImgUploadListener();
        }
        SubmitJsonAsyncTask submitJsonAsyncTask = new SubmitJsonAsyncTask();
        submitJsonAsyncTask.execute(SUBMIT_URL_ADDRESS);
    }

    /**
     * add image
     */

    // 이미지 업데이트 리스너(다이얼로그) //
    private void mImgUpdateListener(){
        // 앨범선택 리스너
        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakePhotoAction();
            }
        };
        // 사진촬영 리스너
        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakeAlbumAction();
            }
        };
        // 취소 리스너
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        // 다이얼로그 세팅
        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진촬영", cameraListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();
    }

    // 카메라 사진 촬영 //
    public void doTakePhotoAction(){ //촬영 후 이미지 가져옴
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //임시 사용 파일 경로 생성
        String temp_file = "temp_"+String.valueOf(System.currentTimeMillis())+".jpg";
        //파일 생성 저장 uri
        mImgCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), temp_file));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImgCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    // 앨범에서 이미지 가져오기 //
    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode){

            // * 사진 촬영 이미지 처리 *
            case PICK_FROM_ALBUM: {
                mImgCaptureUri = data.getData();
                Log.v(TAG, "335:"+mImgCaptureUri.getPath().toString());
            }


            // * 앨범 사진 고르고 이미지 처리 *
            case PICK_FROM_CAMERA: {
                // 이미지 가져옴
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImgCaptureUri, "image/*");

                //크롭 리사이즈 이미지 크기 조정 200*200
                intent.putExtra("outputX", 200); //CROP 한 이미지 X축 크기
                intent.putExtra("outputY",200); //CROP 한 이미지 Y축 크기
                intent.putExtra("aspectX", 1); //CROP BOX X 축 비율
                intent.putExtra("aspectY", 1); //CROP BOX Y 축 비율
                intent.putExtra("scale",true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE); //CROP_FROM_CAMERA case문으로 이동
                break;
            }



            // * 이미지 크롭 이후 이미지 넘겨 받음, 임시파일 삭제 *
            case CROP_FROM_IMAGE: {
                if(resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();
                mImgFileName = null;
                mImgFileName = System.currentTimeMillis()+".jpg";
                //CROP 된 이미지 저장 파일 경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/imgtemp/"+mImgFileName;
                Log.v(TAG, Environment.getExternalStorageDirectory().getAbsolutePath());


                if(extras != null) {
                    Bitmap image = extras.getParcelable("data");
                    // 레이아웃 보여줌
                    mImgAdded.setImageBitmap(image);

                    storeCropImage(image, filePath);
                    mImgAbsolutePath = filePath;
                    break;
                }
                // 임시 파일 삭제
                File f = new File(mImgCaptureUri.getPath());
                if(f.exists()){
                    f.delete();
                }

            }

        }
    }

    // bit map 저장 부분
    private void storeCropImage(Bitmap bitmap, String filePath) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/imgtemp/";
        File dir_imgTemp = new File(dirPath);
        // 임시 디렉토리가 없다면
        if(!dir_imgTemp.exists()){
            dir_imgTemp.mkdir();
        }

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * submit json
     */

    public class SubmitJsonAsyncTask extends AsyncTask<String, String, String> {

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
                jsonObject.accumulate("Title", mTitle);
                jsonObject.accumulate("Kind", mKind);
                jsonObject.accumulate("Contents", mContents);
                jsonObject.accumulate("ImgName", mImgFileName);
                jsonObject.accumulate("Where", mWhereTotal);

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
            if(mServerMsg.equals("err")){
                Toast.makeText(getApplicationContext(), "내부 서버 오류\n다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "신고 완료 !\n홈으로 이동합니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     * upload image file
     */
    private void mImgUploadListener(){
        mDialog = new ProgressDialog(SubmitActivity.this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("이미지 파일 전송 중...");

        mDialog.show();

        Log.v(TAG, "call uploadFile");
        uploadFile(mImgAbsolutePath);
    }

    private void uploadFile(String ImgURL) {
        String url = UPLOAD_IMG_URL_ADDRESS;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .build();

        NetworkService service = retrofit.create(NetworkService.class);
        Log.v(TAG, "upload / img uri : "+ImgURL);
        File photo = new File(ImgURL);
        RequestBody photoBody = RequestBody.create(MediaType.parse("image/jpg"), photo);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", photo.getName(), photoBody);

        String descriptionString = Integer.toString(mUserSessionId);

        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        /**
         * uploading
         */
        Call<ResponseBody> call = service.upload(body, description);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()){

                    Gson gson = new Gson();
                    try {
                        String getResult = response.body().string();

                        JsonParser parser = new JsonParser();
                        JsonElement rootObejct = parser.parse(getResult);

                        UploadResult example = gson.fromJson(rootObejct, UploadResult.class);

                        Log.i(TAG,example.url);

                        String result = example.result;

                        } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, "error : "+e.getMessage());
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"이미지 업로드 실패",Toast.LENGTH_SHORT).show();
                }
                // dismiss dialog
                mDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());

                // dismiss dialog
                mDialog.dismiss();
            }
        });
    }
}
