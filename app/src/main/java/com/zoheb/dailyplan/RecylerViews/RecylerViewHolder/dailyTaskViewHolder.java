package com.zoheb.dailyplan.RecylerViews.RecylerViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfigBuilder;

import com.zoheb.dailyplan.Model.DailyTask;
import com.zoheb.dailyplan.R;


public class dailyTaskViewHolder extends RecyclerView.ViewHolder{

    public TextView startDate,taskName,projectName,assignTo,comments,apptStatusText,endDate,initialsData;
    public Button reactionView;
    public View vw;
    public Context context;
    public RelativeLayout initialsBackgroundColor;


    public dailyTaskViewHolder(View view,Context context) {
        super(view);
        this.context = context;
        vw = view;
        startDate = vw.findViewById(R.id.startDate);
        taskName = vw.findViewById(R.id.taskName);
        projectName = vw.findViewById(R.id.projectName);
        assignTo = vw.findViewById(R.id.assignTo);
        comments = vw.findViewById(R.id.comments);
        apptStatusText = vw.findViewById(R.id.apptStatusText);
        endDate = vw.findViewById(R.id.endDate);
        reactionView = vw.findViewById(R.id.reactionView);
        initialsData = vw.findViewById(R.id.initialsData);
        initialsBackgroundColor = vw.findViewById(R.id.initialsBackgroundColor);

    }

}
