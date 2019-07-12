package com.zoheb.dailyplan.SqlUtils;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.content.Intent;

import com.zoheb.dailyplan.Model.DailyTask;
import com.zoheb.dailyplan.Model.Employee;
import com.zoheb.dailyplan.Model.Project;

import static com.zoheb.dailyplan.CommonUtils.DATABASE_NAME_DAILY_TASK_APP;

@Database(entities = {DailyTask.class, Employee.class, Project.class}, version = 5) //*****Remember to add migration method if changed
@TypeConverters({DateConverter.class})
public abstract class AndroidDbDatasource extends RoomDatabase {
    public abstract DataSyncDao dataSyncDao();

    private static AndroidDbDatasource INSTANCE;
    private static Context cc;

    public static AndroidDbDatasource getDatabase(final Context context) {
        cc=context;
        if (INSTANCE == null) {
            synchronized (AndroidDbDatasource.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AndroidDbDatasource.class, DATABASE_NAME_DAILY_TASK_APP)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }



    private static void upgradeDatabaseWithLogout(){
        cc.deleteDatabase(DATABASE_NAME_DAILY_TASK_APP); //For destructive migration
//        CommonUtils.clearSharedPreferences(cc);
//        Intent i=new Intent(cc, FlashScreen.class);
//        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
//        cc.startActivity(i);
    }
}
