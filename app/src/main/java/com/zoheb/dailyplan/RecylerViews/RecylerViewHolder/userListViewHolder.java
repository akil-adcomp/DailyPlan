package com.zoheb.dailyplan.RecylerViews.RecylerViewHolder;

import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zoheb.dailyplan.R;

public class userListViewHolder extends RecyclerView.ViewHolder  {
    public View vw;
    public TextView initialsText,userNameText,colorText;
    public RelativeLayout initialsBackgroundColor;

    public userListViewHolder(@NonNull View itemView) {
        super(itemView);
        vw = itemView;
        initialsBackgroundColor = vw.findViewById(R.id.initialsBackgroundColor);
        initialsText = vw.findViewById(R.id.initialsData);
        userNameText = vw.findViewById(R.id.userNameText);
        colorText = vw.findViewById(R.id.colorText);
    }
}
