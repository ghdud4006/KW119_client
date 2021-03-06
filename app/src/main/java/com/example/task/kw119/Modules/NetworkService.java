package com.example.task.kw119.Modules;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by kh on 2016. 8. 25..
 */
public interface NetworkService {
    @Multipart
    @POST("/upload")
    Call<ResponseBody> upload(@Part MultipartBody.Part file, @Part("name") RequestBody description);

}
