package com.zoheb.dailyplan.RecylerViews.RecylerViewAdapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.zoheb.dailyplan.CommonUtils;
import com.zoheb.dailyplan.MainActivity;
import com.zoheb.dailyplan.Model.DailyTask;
import com.zoheb.dailyplan.R;
import com.zoheb.dailyplan.RecylerViews.RecylerViewHolder.dailyTaskViewHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.goodiebag.carouselpicker.CarouselPicker;

import static com.zoheb.dailyplan.CommonUtils.CANCELLED;
import static com.zoheb.dailyplan.CommonUtils.COMPLETED;
import static com.zoheb.dailyplan.CommonUtils.IN_PROGRESS;
import static com.zoheb.dailyplan.CommonUtils.PENDING_FROM_CLIENT;
import static com.zoheb.dailyplan.CommonUtils.PENDING_GO_LIVE;
import static com.zoheb.dailyplan.CommonUtils.getDaysLeft;
import static com.zoheb.dailyplan.CommonUtils.getSimpleDate2;
import static com.zoheb.dailyplan.CommonUtils.getUserColorCode;


public class dailyTasksViewAdapter extends RecyclerView.Adapter<dailyTaskViewHolder> implements Filterable {

    List<DailyTask> dailyTaskDTO;
    Context context;
    Dialog dialog;

    public dailyTasksViewAdapter(List<DailyTask> mdailyTaskDTO, Context context) {
        this.dailyTaskDTO = mdailyTaskDTO;
        this.context = context;
    }

    @NonNull
    @Override
    public dailyTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.daily_task_card, parent, false);
        return new dailyTaskViewHolder(itemView,context);
    }

    @Override
    public void onBindViewHolder(@NonNull final dailyTaskViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final DailyTask dtTask = dailyTaskDTO.get(position);
        holder.startDate.setText(CommonUtils.getSimpleDate2(dtTask.getStartDate()));
        holder.apptStatusText.setText(dtTask.getStatus());
        holder.assignTo.setText("Assigned To : "+dtTask.getAssignTo());
        holder.initialsData.setText(dtTask.getProject().substring(0,1));
        try {
            ((GradientDrawable)holder.initialsBackgroundColor.getBackground()).setColor(Color.parseColor(dtTask.getColorCode()));
        }catch (Exception e){
            ((GradientDrawable)holder.initialsBackgroundColor.getBackground()).setColor(getUserColorCode(context));
        }
        Date endDate = dtTask.getEndDate();
        holder.endDate.setText(String.format("%s Day's left. Ends on %s", getDaysLeft(endDate), getSimpleDate2(endDate)));
        holder.taskName.setText("Task :"+dtTask.getTask());
        holder.projectName.setText(dtTask.getProject());
        holder.reactionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(dtTask.getSrNo(), position, dtTask.getTask());
            }
        });

//        holder.reactionView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectedPosition = position;
//                holder.popup.showAtLocation(v, Gravity.NO_GRAVITY,0,0);
//            }
//        });
//        holder.reactionViewDummy.setOnTouchListener(holder.popup);

//        holder.popup.setReactionSelectedListener(new Function1<Integer, Boolean>() {
//            @Override
//            public Boolean invoke(Integer pos) {
//                Log.i("Reactions", "Selection position=" + selectedPosition);
//
//                dialyTask dt =  dailyTaskDTO.get(selectedPosition);
//
//
//                if(pos>0)
//                    ((MainActivity) context).updateTaskStatus(dt.getSrNo(),"Completed",selectedPosition);
//
//                // Close selector if not invalid item (testing purpose)
//                return true;
//            }
//        });
    }


    public void updateData(List<DailyTask> dailyTaskDTO){
        this.dailyTaskDTO = new ArrayList<>(dailyTaskDTO);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.dailyTaskDTO.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            //returns the text to display in text view
            @Override
            public String convertResultToString(Object resultValue) {
                return ((String) resultValue);
            }
            //Filters the list according to condition
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<DailyTask> departmentsSuggestion = new ArrayList<>();

                if (constraint != null) {
                    String[] searchConstraint=constraint.toString().toLowerCase().split(" ");
                    for (DailyTask dt : dailyTaskDTO) {
                        if (dt.getTask().toLowerCase().replaceAll("\\s+","").contains(searchConstraint[searchConstraint.length-1]) ||
                                dt.getProject().toLowerCase().replaceAll("\\s+","").contains(searchConstraint[searchConstraint.length-1])) {
                            departmentsSuggestion.add(dt);
                        }
                    }
                    filterResults.values = departmentsSuggestion;
                    filterResults.count = departmentsSuggestion.size();
                }
                return filterResults;
            }
            //Publishes the list into dropdown
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                dailyTaskDTO.clear();
                // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
                for (Object object : (List<?>) results.values) {
                    if (object instanceof DailyTask) {
                        dailyTaskDTO.add((DailyTask) object);
                    }
                }
                notifyDataSetChanged();

            }
        };
    }

    public void showDialog(final int srNo, final int position,String taskNameText) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_test);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();
        final TextView statusText = dialog.findViewById(R.id.statusText);
        Button submitBtn = dialog.findViewById(R.id.submitBtn);
        ImageView rightArrow = dialog.findViewById(R.id.rightArrow);
        ImageView leftArrow = dialog.findViewById(R.id.leftArrow);
        TextView taskName = dialog.findViewById(R.id.taskName);
        taskName.setText(taskNameText);
        final CarouselPicker carouselPicker = dialog.findViewById(R.id.carousel);
        // Case 1 : To populate the picker with images
        List<CarouselPicker.PickerItem> imageItems = new ArrayList<>();
        imageItems.add(new CarouselPicker.DrawableItem(R.mipmap.ic_in_progress));
        imageItems.add(new CarouselPicker.DrawableItem(R.mipmap.ic_pending_go_live));
        imageItems.add(new CarouselPicker.DrawableItem(R.mipmap.ic_cancelled));
        imageItems.add(new CarouselPicker.DrawableItem(R.mipmap.ic_client_pending));
        imageItems.add(new CarouselPicker.DrawableItem(R.mipmap.ic_completed_task));


//Create an adapter
        CarouselPicker.CarouselViewAdapter imageAdapter = new CarouselPicker.CarouselViewAdapter(context, imageItems, 0);
//Set the adapter
        carouselPicker.setAdapter(imageAdapter);
        carouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //position of the selected item
                switch (position){
                    case 0:
                        statusText.setText(IN_PROGRESS);
                        break;
                    case 1:
                        statusText.setText(PENDING_GO_LIVE);
                        break;
                    case 2:
                        statusText.setText(CANCELLED);
                        break;
                    case 3:
                        statusText.setText(PENDING_FROM_CLIENT);
                        break;
                    case 4:
                        statusText.setText(COMPLETED);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        carouselPicker.setCurrentItem(2);
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carouselPicker.setCurrentItem(carouselPicker.getCurrentItem()+1);
            }
        });
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                carouselPicker.setCurrentItem(carouselPicker.getCurrentItem()-1);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("srNo "+srNo+" position "+position);

                ((MainActivity) context).updateTaskStatus(srNo,statusText.getText().toString(),position);
                dialog.dismiss();
            }
        });

    }

}
