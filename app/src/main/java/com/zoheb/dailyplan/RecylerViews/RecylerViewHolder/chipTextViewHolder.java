package com.zoheb.dailyplan.RecylerViews.RecylerViewHolder;

import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zoheb.dailyplan.R;

public class chipTextViewHolder extends RecyclerView.ViewHolder {
    public View vw;
    public Chip chipText;

    public chipTextViewHolder(@NonNull View itemView) {
        super(itemView);
        vw = itemView;
        chipText = vw.findViewById(R.id.chipText);
    }
}
