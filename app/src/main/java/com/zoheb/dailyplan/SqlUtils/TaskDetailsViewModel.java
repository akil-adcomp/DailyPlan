package com.zoheb.dailyplan.SqlUtils;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.zoheb.dailyplan.Model.DailyTask;
import java.util.List;

import static com.zoheb.dailyplan.CommonUtils.CANCELLED;
import static com.zoheb.dailyplan.CommonUtils.COMPLETED;
import static com.zoheb.dailyplan.CommonUtils.notNullOrEmpty;

public class TaskDetailsViewModel extends AndroidViewModel {
    private DataSyncDao dataSyncDao;

    public TaskDetailsViewModel(@NonNull Application application) {
        super(application);
        dataSyncDao = AndroidDbDatasource.getDatabase(application).dataSyncDao();
    }

    public LiveData<List<DailyTask>> getAllPendingTasks(String userName){
        if(!userName.equals("Anonymous"))
            return dataSyncDao.getPendingTasksByUser(new String[]{CANCELLED,COMPLETED},userName);
        else
            return dataSyncDao.getPendingTasks(new String[]{CANCELLED,COMPLETED});

    }
}
