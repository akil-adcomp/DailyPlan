package com.zoheb.dailyplan.Retrofit;

import com.zoheb.dailyplan.CommonUtils;
import com.zoheb.dailyplan.Model.AllMasterSync;
import com.zoheb.dailyplan.Model.taskList;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface UserService {

    @GET(CommonUtils.url2+"exec?action=getUserDetails")
    Call<taskList> getAllTaskList();

    @GET(CommonUtils.url2+"exec?action=getMastersToSync")
    Call<AllMasterSync> getAllMastersList();

    @POST(CommonUtils.url2+"exec")
    Call<ResponseBody> createTask(@Body RequestBody user);

    @POST(CommonUtils.url2+"exec")
    Call<ResponseBody> updateTask(@Body RequestBody user);
}