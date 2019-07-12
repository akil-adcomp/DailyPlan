package com.zoheb.dailyplan.Model;

import java.io.Serializable;
import java.util.List;

public class taskList implements Serializable {
    List<DailyTask> sheet1;

    public List<DailyTask> getDialyTasks() {
        return sheet1;
    }

    public void setDialyTasks(List<DailyTask> dialyTasks) {
        this.sheet1 = dialyTasks;
    }

    @Override
    public String toString() {
        return "taskList{" +
                "Sheet1=" + sheet1 +
                '}';
    }
}
