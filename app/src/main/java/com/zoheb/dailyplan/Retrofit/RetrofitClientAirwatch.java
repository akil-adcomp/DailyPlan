package com.zoheb.dailyplan.Retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Zoheb K Shah on 27/6/18.
 */
public class RetrofitClientAirwatch {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl, final Context context) {

       TokenManager tokenManager=new TokenManager() {
            @Override
            public String getToken() {
                return null;
            }

            @Override
            public boolean hasToken() {
                return false;
            }

            @Override
            public void clearToken() {

            }

            @Override
            public String refreshToken() {

                return null;
            }
        };

        CookieJar okhttpCookieJar = new CookieJar() {

            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        };
        OkHttpClient client =new OkHttpClient.Builder()
                 .build();


        if (retrofit==null) {

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;

    }

//     try {
//        Authenticator awAuthenticator = new AWOkHttpAuthenticator(false);
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(new TokenInterceptor(tokenManager))
//                .cookieJar(okhttpCookieJar)
//                .sslSocketFactory(sslContext.getSocketFactory(), trustAllTrustManager)
//                .authenticator(awAuthenticator)
//                .build();
//        if (retrofit == null) {
//
//            Gson gson = new GsonBuilder()
//                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
//                    .create();
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .client(client)
//                    .build();
//        }
//        return retrofit;
//    }catch (Exception e){
//        e.printStackTrace();
//    }
//        return retrofit;

    private static X509TrustManager trustAllTrustManager = new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    };


    //Old
//    OkHttpClient client =new OkHttpClient.Builder()
//            .addInterceptor(new TokenInterceptor(tokenManager))
//            .build();
//        if (retrofit==null) {
//
//        Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd HH:mm:ss")
//                .create();
//        retrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build();
//    }
//        return retrofit;

    private static class BasicAuthInterceptor implements Interceptor {

        private String credentials;

        public BasicAuthInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }

    }


    private static class TokenInterceptor implements Interceptor {
        private final TokenManager mTokenManager;

        private TokenInterceptor(TokenManager tokenManager) {
            mTokenManager = tokenManager;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request modifiedRequest = request;

            Response response = chain.proceed(modifiedRequest);
            boolean unauthorized = response.code() == 401;
            if (unauthorized) {
                String newToken = mTokenManager.refreshToken();
                modifiedRequest = request.newBuilder()
                        .header("token", newToken)
                        .build();
                return chain.proceed(modifiedRequest);
            }
            return response;
        }
    }

    interface TokenManager {
        String getToken();
        boolean hasToken();
        void clearToken();
        String refreshToken();
    }
}
