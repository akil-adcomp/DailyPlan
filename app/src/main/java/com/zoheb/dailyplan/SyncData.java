package com.zoheb.dailyplan;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.zoheb.dailyplan.Model.AllMasterSync;
import com.zoheb.dailyplan.Retrofit.CallUtils;
import com.zoheb.dailyplan.Retrofit.RetrofitClient;
import com.zoheb.dailyplan.Retrofit.UserService;
import com.zoheb.dailyplan.SqlUtils.AndroidDbDatasource;
import com.zoheb.dailyplan.SqlUtils.DataSyncDao;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zoheb.dailyplan.CommonUtils.DEFAULT_NOTIFICATION_CHANNEL_ID;
import static com.zoheb.dailyplan.CommonUtils.createGetNotificationChannel;
import static com.zoheb.dailyplan.CommonUtils.url;

public class SyncData extends Service {

    Notification notification;
    int queueCount = 0;
    int totalQueueCount = 5;
    private int toastFlag = 0, successCount = 0;
    private String lastModfiedDateBrand, lastModfiedDateSku;
    private String lastModifiedDateVaraint;
    private String lastModifiedDateLanguage, lastModifiedDateDoctor;
    private Boolean receiverRegistered, isDataSyncComplete = false,isFailed=false;
    AndroidDbDatasource db;
    DataSyncDao sd;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        moveToForeground();
        receiverRegistered = false;
        getAllMasters();
//        new SyncDbtask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        isFailed=false;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
            stopForeground(true);
            this.stopSelf();
            super.onDestroy();

    }

    private void moveToForeground() {
        String channelId="";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId= createGetNotificationChannel(this,DEFAULT_NOTIFICATION_CHANNEL_ID);
        }
        notification = new NotificationCompat.Builder(this)
                .setChannelId(channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle("Daily task")
                .setContentText("Synchronising data...")
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), 0))
                .build();
        startForeground(1, notification);
    }


    public void getAllMasters(){


        UserService userService = RetrofitClient.getClient(url, getApplicationContext()).create(UserService.class);

        Call<AllMasterSync> tCall=userService.getAllMastersList();
        CallUtils.enqueueWithRetry(tCall,new Callback<AllMasterSync>() {
            @Override
            public void onResponse(Call<AllMasterSync> call, Response<AllMasterSync> response) {
                System.out.println(response.body() + "dfsdsf" + response.isSuccessful());
                System.out.println("***********&&&&&&&&&&&&&"+response.body());

                if(response.isSuccessful()) {
                    AllMasterSync allMasterSyncByLastModDateDTO = response.body();
                    new insertAllMasters(allMasterSyncByLastModDateDTO).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else {
                    onDestroy();
                }
            }

            @Override
            public void onFailure(Call<AllMasterSync> call, Throwable t) {
                System.out.println("*******&&&&"+t.getMessage());

                onDestroy();

            }
        });
    }

    private class insertAllMasters extends AsyncTask<Void, Void, Void> {
        AllMasterSync allDTO;

        public insertAllMasters(AllMasterSync allDTO) {
            this.allDTO = allDTO;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            db= AndroidDbDatasource.getDatabase(getApplicationContext());
            sd=db.dataSyncDao();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            sd.insertAllProjects(allDTO.getProjectList());
            sd.insertAllEmployees(allDTO.getEmployeeList());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
          onDestroy();
        }
    }


    private void createDownloadFailedNotification() {
//        if (!receiverRegistered) {
//            Intent closeButton = new Intent("Download_Failed");
//            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(SyncData.this, 0, closeButton, 0);
//            downloadCancelReceiver = new DownloadCancelReceiver();
//            notification = new NotificationCompat.Builder(SyncData.this)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle("DC App Error")
//                    .setContentText("Synchronization failed !!")
//                    .setPriority(Notification.PRIORITY_HIGH)
//                    .setDefaults(Notification.DEFAULT_SOUND)
//
//    .addAction(R.drawable.ic_refresh, "retry", pendingSwitchIntent)
//                    .build();
//            isFailed=true;
//            setSyncDataFailed(SyncData.this, true);
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(1, notification);
//            IntentFilter filter = new IntentFilter();
//            filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//            registerReceiver(downloadCancelReceiver, filter);
//            receiverRegistered = true;
//        }

    }

}