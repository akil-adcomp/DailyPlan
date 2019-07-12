package com.zoheb.dailyplan.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "project")
public class Project implements Serializable {

    @PrimaryKey
    @NonNull
    private Integer srNo;
    private String projectName;
    private int score;
    private String colorCode;

    public Integer getSrNo() {
        return srNo;
    }

    public void setSrNo(Integer srNo) {
        this.srNo = srNo;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    @Override
    public String toString() {
        return "Project{" +
                "srNo=" + srNo +
                ", projectName='" + projectName + '\'' +
                ", score=" + score +
                ", colorCode='" + colorCode + '\'' +
                '}';
    }
}
