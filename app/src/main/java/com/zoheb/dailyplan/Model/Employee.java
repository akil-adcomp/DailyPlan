package com.zoheb.dailyplan.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "employee")
public class Employee implements Serializable {

    @PrimaryKey
    @NonNull
    private Integer srNo;
    private String employeeName;
    private int score;

    public Integer getSrNo() {
        return srNo;
    }

    public void setSrNo(Integer srNo) {
        this.srNo = srNo;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "srNo=" + srNo +
                ", employeeName='" + employeeName + '\'' +
                '}';
    }
}
