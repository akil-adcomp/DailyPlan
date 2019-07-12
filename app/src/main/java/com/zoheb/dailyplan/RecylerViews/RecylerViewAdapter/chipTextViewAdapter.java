package com.zoheb.dailyplan.RecylerViews.RecylerViewAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;

import com.zoheb.dailyplan.R;
import com.zoheb.dailyplan.RecylerViews.RecylerViewHolder.chipTextViewHolder;

import java.util.ArrayList;
import java.util.List;

public class chipTextViewAdapter extends RecyclerView.Adapter<chipTextViewHolder> implements Filterable {
    List<String> chipTextData,chipTextDataAll;
    EditText editText;

    public chipTextViewAdapter(List<String> chipTextData, EditText editText) {
        this.chipTextData = chipTextData;
        this.chipTextDataAll = chipTextData;
        this.editText = editText;
    }

    @NonNull
    @Override
    public chipTextViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chiptext, viewGroup, false);
        return new chipTextViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull chipTextViewHolder chipTextViewHolder, final int i) {
        chipTextViewHolder.chipText.setText(chipTextData.get(i));
        chipTextViewHolder.chipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(chipTextData.get(i));
            }
        });

    }

    @Override
    public int getItemCount() {
        return chipTextData.size();
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
                List<String> departmentsSuggestion = new ArrayList<>();

                if (constraint != null) {
                    String[] searchConstraint=constraint.toString().toLowerCase().split(" ");
                    System.out.println("(((((*****"+chipTextDataAll.size());
                    for (String apt : chipTextDataAll) {
                        if (apt.toLowerCase().replaceAll("\\s+","").contains(searchConstraint[searchConstraint.length-1])) {
                            departmentsSuggestion.add(apt);
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
                chipTextData.clear();
                // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
                for (Object object : (List<?>) results.values) {
                    if (object instanceof String) {
                        chipTextData.add((String) object);
                    }
                }
                notifyDataSetChanged();

            }
        };
    }

}
