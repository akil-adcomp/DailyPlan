package com.zoheb.dailyplan.Model;

import java.io.Serializable;
import java.util.List;

public class AllMasterSync implements Serializable {

    List<Project> projectList;
    List<Employee> employeeList;

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    @Override
    public String toString() {
        return "AllMasterSync{" +
                "projectList=" + projectList +
                ", employeeList=" + employeeList +
                '}';
    }
}
