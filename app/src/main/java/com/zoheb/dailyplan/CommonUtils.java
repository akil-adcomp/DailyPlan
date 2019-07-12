package com.zoheb.dailyplan;


import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static java.time.temporal.ChronoUnit.DAYS;


public class CommonUtils {

    public static String dailyTaskSheetId = "18o7aLxyOtNw4hAuJAZwzUJ5Q51YGFVAnLvy3iQoroY8";
    public static String dailyTaskSheetName = "sheet1";
    public static final String PREFERENCES_NAME = "DailyTaskPrefs";//jskjksfksfkskgjiiroierk


    public static final String IN_PROGRESS = "In Progress";
    public static final String PENDING_GO_LIVE = "Pending Go Live";
    public static final String CANCELLED = "Cancelled";
    public static final String PENDING_FROM_CLIENT = "Pending from client";
    public static final String COMPLETED = "Completed";
    static Random r=new Random();


    public static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "daily_task_sync_channel";
    public static final String DATABASE_NAME_DAILY_TASK_APP="dailytask.db";
    private static int[] color = {Color.parseColor("#A72200"),Color.parseColor("#3B8F00"),
            Color.parseColor("#0059A7"),Color.parseColor("#A74E00"),
            Color.parseColor("#2D00A7"),Color.parseColor("#5E0174"),
            Color.parseColor("#646001")};



    public static final String url = "https://script.google.com/macros/s/AKfycbxOLElujQcy1-ZUer1KgEvK16gkTLUqYftApjNCM_IRTL3HSuDk/";
    public static final String url2 = "https://script.google.com/macros/s/AKfycbzPlzeBEm1ECj3Pe7Wq0ZiWEgZn-0skooxI7h3ezgPitmUMB43H/";

    public static Map<String,String> getDailyTaskQueryParam(){
        Map<String,String> taskList = new HashMap<>();
        taskList.put("sheet",dailyTaskSheetName);
        taskList.put("id",dailyTaskSheetId);
        return taskList;
    }

    public static int getUserColorCode(Context cntx) {
        SharedPreferences settings = cntx.getSharedPreferences(PREFERENCES_NAME, 0);
        return settings.getInt("userColorCode", getRandomColor());
    }
    public static void setUserColorCode(Context context, int colorCode) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("userColorCode", colorCode);
        editor.commit();
    }

    public static String getUserName(Context cntx) {
        SharedPreferences settings = cntx.getSharedPreferences(PREFERENCES_NAME, 0);
        return settings.getString("userName", "Anonymous");
    }
    public static void setUserName(Context context, String userName) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userName", userName);
        editor.commit();
    }


    public static String getSimpleDate2(Date d){
        try {
            CharSequence s = android.text.format.DateFormat.format("dd-MMM yyyy",d.getTime());
            return s.toString();
        }catch (Exception e){
            return "";
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static  String createGetNotificationChannel(Context context, String channelId){
        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null:"Notification Manager is null";
        if(notificationManager.getNotificationChannel(channelId)==null) {
            String channelName = "Notification Background Service";
            NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(chan);
        }
        return channelId;
    }

    public static String getDaysLeft(Date endDate) {

        try {
        Date todayWithZeroTime = new Date();

        int cYear = 0, cMonth = 0, cDay = 0;

        if (todayWithZeroTime.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }

        Calendar eCal = Calendar.getInstance();
        eCal.setTime(endDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return ""+(int) dayCount;

        }catch (Exception e){
            return "";
        }
    }

    public static String getSimpleDate(Date d){
        try {
            CharSequence s = android.text.format.DateFormat.format("MM/dd/yyyy",d.getTime());
            return s.toString();
        }catch (Exception e){
            return "";
        }
    }

    public static boolean notNullOrEmpty(String field){
        return field != null && !field.replaceAll("\\s+","").equals("");
    }

    public static boolean notNullOrEmpty(EditText editText){
        String field = editText.getText().toString();
        return field != null && !field.replaceAll("\\s+","").equals("");
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int getRandomColor(){

        int randomNumber=r.nextInt(color.length);
        return color[randomNumber];
    }

}
