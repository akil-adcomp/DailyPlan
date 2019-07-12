package com.zoheb.dailyplan.Retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static long timeout = 180;
    private static TokenManager tokenManager = new TokenManager() {
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
        public String refreshToken(Context context) {

            return null;
        }
    };
    private static X509TrustManager trustAllTrustManager = new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    };

    public static Retrofit getClient(String baseUrl, final Context context) {

        OkHttpClient client = null;
            client = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .build();



            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class,new DateDeserializer())
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();

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

    public static Retrofit getClientWithProgress(String baseUrl, final Context context, final UploadProgressRequestBody.ProgressListener progressListener) {

        OkHttpClient client = null;
        if (baseUrl.contains("awmdm")) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .addInterceptor(new BasicAuthInterceptor("Zoheb", "Vmware@123"))
                    .build();
        } else {

            client = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .addInterceptor(new TokenInterceptor(tokenManager, context))
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request originalRequest = chain.request();

                            if (originalRequest.body() == null) {
                                return chain.proceed(originalRequest);
                            }

                            Request progressRequest = originalRequest.newBuilder()
                                    .method(originalRequest.method(),
                                            new UploadProgressRequestBody(originalRequest.body(), progressListener))
                                    .build();

                            return chain.proceed(progressRequest);
                        }
                    })
                    .build();
        }

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

    }


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

    interface TokenManager {
        String getToken();

        boolean hasToken();

        void clearToken();

        String refreshToken(Context context);
    }

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
        private final Context mContext;

        public TokenInterceptor(TokenManager mTokenManager, Context mContext) {
            this.mTokenManager = mTokenManager;
            this.mContext = mContext;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request modifiedRequest = request;

            Response response = chain.proceed(modifiedRequest);
            boolean unauthorized = response.code() == 401;
            if (unauthorized) {
                String newToken = mTokenManager.refreshToken(mContext);
                modifiedRequest = request.newBuilder()
                        .header("token", newToken!=null ? newToken : "invalid_token")
                        .build();
                return chain.proceed(modifiedRequest);
            }
            return response;
        }
    }
}