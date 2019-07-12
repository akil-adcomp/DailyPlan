package com.zoheb.dailyplan.RecylerViews.RecylerViewAdapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zoheb.dailyplan.MainActivity;
import com.zoheb.dailyplan.R;
import com.zoheb.dailyplan.RecylerViews.RecylerViewHolder.chipTextViewHolder;
import com.zoheb.dailyplan.RecylerViews.RecylerViewHolder.userListViewHolder;

import java.util.List;

import static com.zoheb.dailyplan.CommonUtils.getRandomColor;
import static com.zoheb.dailyplan.CommonUtils.getUserName;
import static com.zoheb.dailyplan.CommonUtils.setUserColorCode;
import static com.zoheb.dailyplan.CommonUtils.setUserName;

public class UserNameRecyclerViewAdapter extends RecyclerView.Adapter<userListViewHolder> {
    List<String> employeeList;
    Context context;

    public UserNameRecyclerViewAdapter(List<String> employeeList, Context context) {
        this.employeeList = employeeList;
        this.context = context;
    }

    @NonNull
    @Override
    public userListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_user_list, viewGroup, false);

        return new userListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final userListViewHolder userListViewHolder, int i) {
        final String employeeName = employeeList.get(i);
        userListViewHolder.userNameText.setText(employeeName);
        userListViewHolder.initialsText.setText(employeeName.substring(0,1));
        int color = getRandomColor();
        ((GradientDrawable)userListViewHolder.initialsBackgroundColor.getBackground()).setColor(color);
        userListViewHolder.colorText.setText(color+"");
        userListViewHolder.vw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserName(context,employeeName);
                setUserColorCode(context, Integer.parseInt(userListViewHolder.colorText.getText().toString()));
                ((MainActivity) context).setData();
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }
}
