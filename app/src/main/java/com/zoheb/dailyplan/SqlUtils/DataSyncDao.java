package com.zoheb.dailyplan.SqlUtils;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.zoheb.dailyplan.CommonUtils;
import com.zoheb.dailyplan.Model.DailyTask;
import com.zoheb.dailyplan.Model.Employee;
import com.zoheb.dailyplan.Model.Project;

import java.util.Date;
import java.util.List;


@Dao
public interface DataSyncDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTasks(List<DailyTask> brands);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllProjects(List<Project> projectList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllEmployees(List<Employee> employeeList);

    @Query("select t.*,p.colorCode as colorCode from task t " +
            "inner join project p on p.projectName = t.project " +
            "where t.status not in (:filter) order by t.srNo desc")
    LiveData<List<DailyTask>> getPendingTasks(String[] filter);

    @Query("select t.*,p.colorCode as colorCode from task t " +
            "inner join project p on p.projectName = t.project " +
            "where t.status not in (:filter) and t.assignTo=:userName order by t.srNo desc")
    LiveData<List<DailyTask>> getPendingTasksByUser(String[] filter,String userName);

    @Query("Update task set status = :status where srNo = :srNo")
    void updateTask(String status,int srNo);

    @Query("Select projectName from project p order by p.score desc")
    List<String> getAllProjectByScore();

    @Query("Select employeeName from employee e order by e.score desc")
    List<String> getAllEmployeeByScore();

    @Query("Update employee set score = score+1 where employeeName = :employeeName")
    void incrementScoreByNameForEmployee(String employeeName);

    @Query("Update project set score = score+1 where projectName = :projectName")
    void incrementScoreByNameForProject(String projectName);
}
