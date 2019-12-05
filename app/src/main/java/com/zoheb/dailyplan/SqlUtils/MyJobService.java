package com.zoheb.dailyplan.SqlUtils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.zoheb.dailyplan.CommonUtils;
import com.zoheb.dailyplan.MainActivity;
import com.zoheb.dailyplan.Model.taskList;
import com.zoheb.dailyplan.Retrofit.CallUtils;
import com.zoheb.dailyplan.Retrofit.RetrofitClient;
import com.zoheb.dailyplan.Retrofit.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zoheb.dailyplan.Retrofit.RetrofitErrorHelper.parseNetworkError;

public class MyJobService extends JobService {
    Notification notification;
    private Boolean receiverRegistered, isDataSyncComplete = false,isFailed=false;
    private JobParameters jbp;
    private int failedCount=0;
    AndroidDbDatasource db;


    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(JobParameters job) {
        this.jbp=job;
        // Do some work here
        receiverRegistered = false;


        isFailed=false;
        return true;// Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        System.out.println("Build version outside");

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            System.out.println("Build version inside");
//            stopForeground(true);
//            this.stopSelf();
//            super.onDestroy();
//        }
        return false; // Answers the question: "Should this job be retried?"
    }


    public void getAllTaskList(){
        UserService userService = RetrofitClient.getClient(CommonUtils.url, MyJobService.this).create(UserService.class);
        Call<taskList> tCall=userService.getAllTaskList();
        CallUtils.enqueueWithRetry(tCall,new Callback<taskList>() {
            @Override
            public void onResponse(Call<taskList> call, Response<taskList> response) {


                if(response.isSuccessful()) {
                    taskList daDialyTask = response.body();
//                    dataSyncDao.insertAllTasks(daDialyTask.getDialyTasks());

                }else {

                }
            }

            @Override
            public void onFailure(Call<taskList> call, Throwable t) {
                parseNetworkError(MyJobService.this,t);
            }
        });
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
//                    .addAction(R.drawable.ic_refresh, "retry", pendingSwitchIntent)
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