package com.zoheb.dailyplan.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "task")
public class DailyTask implements Serializable {

    @PrimaryKey
    @NonNull
    private int srNo;
    private String task;
    private String project;
    private Date startDate;
    private Date endDate;
    private String assignTo;
    private String status;
    private String comments;
    private String colorCode;

    public int getSrNo() {
        return srNo;
    }

    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    @Override
    public String toString() {
        return "DailyTask{" +
                "srNo=" + srNo +
                ", project='" + project + '\'' +
                '}';
    }
//    @Override
//    public String toString() {
//        return "DailyTask{" +
//                "srNo=" + srNo +
//                ", task='" + task + '\'' +
//                ", project='" + project + '\'' +
//                ", startDate=" + startDate +
//                ", endDate=" + endDate +
//                ", assignTo='" + assignTo + '\'' +
//                ", status='" + status + '\'' +
//                ", comments='" + comments + '\'' +
//                ", colorCode='" + colorCode + '\'' +
//                '}';
//    }
}
