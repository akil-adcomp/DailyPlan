package com.zoheb.dailyplan.Retrofit;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.ConnectException;

import retrofit2.Response;

public class RetrofitErrorHelper<T> {


    public static ResponseFormat parseError(Context context, Response<?> response) {
        String constError="unknown error occured";
        ResponseFormat responseFormat = new ResponseFormat();
        System.out.println("response.errorBody().charStream() "+response.toString());
        try {
            Gson gson = new Gson();
            responseFormat = gson.fromJson(response.errorBody().charStream(), ResponseFormat.class);
        } catch (Exception e) {
        }
        try{
            String r=responseFormat.getFailedReason();
            Toast.makeText(context, r.isEmpty() ? constError:r, Toast.LENGTH_LONG).show();
        }catch (Exception e) {

            Toast.makeText(context, constError, Toast.LENGTH_LONG).show();
        }
        return responseFormat;
    }

    public static void parseNetworkError(Context context, Throwable t){
        System.out.println(")))____"+t.getLocalizedMessage());
        try {
            if (t instanceof ConnectException) {
                Toast.makeText(context, "No Internet", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Connection timeout or network is not connected", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ignored){}
    }
}