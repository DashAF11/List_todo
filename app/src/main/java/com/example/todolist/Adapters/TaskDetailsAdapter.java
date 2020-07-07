package com.example.todolist.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Entities.TaskDetailsEntity;
import com.example.todolist.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.graphics.Color.rgb;

public class TaskDetailsAdapter extends RecyclerView.Adapter<TaskDetailsAdapter.ViewHolder> implements Filterable {

    Context context;
    Calendar calendar = Calendar.getInstance();
    List<TaskDetailsEntity> detailsEntities;
    List<TaskDetailsEntity> detailsSearchEntities;
    RecyclerClickListener clickListener;

    public TaskDetailsAdapter(Context context, RecyclerClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    public void setTaskList(List<TaskDetailsEntity> taskDetailsEntities) {
        this.detailsEntities = taskDetailsEntities;
        this.detailsSearchEntities = new ArrayList<>(taskDetailsEntities);
        Timber.d("categoryEntities_Size : %s", detailsEntities.size());
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_recyclerview_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskDetailsEntity taskDetailsEntity = detailsEntities.get(position);

        holder.taskName_TextView.setText(taskDetailsEntity.getTask_name());
        holder.taskID_TextView.setText("" + taskDetailsEntity.getTask_id());
        holder.taskDate_TextView.setText(taskDetailsEntity.getTask_date());
        holder.taskTime_TextView.setText(taskDetailsEntity.getTask_time());
        if (detailsEntities.get(position).getTask_date().equals("false")) {
            holder.taskDate_TextView.setVisibility(View.GONE);
            holder.taskTime_TextView.setVisibility(View.GONE);
        } else {
            boolean today = DateUtils.isToday(detailsEntities.get(position).getTimestamp());
            Timber.d("isToday : " + today);
            if (today) {
                holder.taskDate_TextView.setText("Today");
            } else {
                holder.taskDate_TextView.setText(taskDetailsEntity.getTask_date());
            }
        }


        if (detailsEntities.get(position).getTask_alarm().equals("true")) {
            holder.alarmTask_ImageView.setVisibility(View.VISIBLE);
        } else {
            holder.alarmTask_ImageView.setVisibility(View.GONE);
        }

        if (detailsEntities.get(position).getTimestamp() <= calendar.getTimeInMillis()) {
            holder.delayTask_ImageView.setVisibility(View.VISIBLE);
        } else {
            holder.delayTask_ImageView.setVisibility(View.GONE);
        }

        if (detailsEntities.get(position).getTask_priority().equals("high")) {
            holder.priority_color_View.setBackgroundColor(rgb(230, 0, 0));
        } else if (detailsEntities.get(position).getTask_priority().equals("med")) {
            holder.priority_color_View.setBackgroundColor(rgb(255, 128, 0));
        } else if (detailsEntities.get(position).getTask_priority().equals("low")) {
            holder.priority_color_View.setBackgroundColor(rgb(0, 128, 0));
        }

        if (detailsEntities.get(position).getTask_done_status().equals("true")) {
            holder.task_CheckBox.setChecked(true);
            holder.taskName_TextView.setPaintFlags(holder.taskName_TextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.task_CheckBox.setChecked(false);
        }

        holder.task_CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                holder.taskName_TextView.setPaintFlags(holder.taskName_TextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                clickListener.checkBoxClickListener(detailsEntities.get(position).getTask_id(), "true");
            } else {
                holder.taskName_TextView.setPaintFlags(holder.taskName_TextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                clickListener.checkBoxClickListener(detailsEntities.get(position).getTask_id(), "false");
            }
        });

        holder.itemView.setTag(R.id.catId, taskDetailsEntity.getTask_catID());
        holder.itemView.setTag(R.id.taskId, taskDetailsEntity.getTask_id());
        holder.itemView.setTag(R.id.taskName, taskDetailsEntity.getTask_name());
        holder.itemView.setTag(R.id.taskDetails, taskDetailsEntity);
    }

    @Override
    public int getItemCount() {
        return detailsEntities.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<TaskDetailsEntity> filterList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filterList.addAll(detailsSearchEntities);
            } else {
                for (TaskDetailsEntity taskDetailsEntity : detailsSearchEntities) {
                    if (taskDetailsEntity.getTask_name().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filterList.add(taskDetailsEntity);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            detailsEntities.clear();
            detailsEntities.addAll((Collection<? extends TaskDetailsEntity>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.taskID_TextView)
        TextView taskID_TextView;
        @BindView(R.id.taskName_TextView)
        TextView taskName_TextView;
        @BindView(R.id.taskDate_TextView)
        TextView taskDate_TextView;
        @BindView(R.id.taskTime_TextView)
        TextView taskTime_TextView;

        @BindView(R.id.delayTask_ImageView)
        ImageView delayTask_ImageView;
        @BindView(R.id.alarmTask_ImageView)
        ImageView alarmTask_ImageView;

        @BindView(R.id.priority_color_View)
        ConstraintLayout priority_color_View;

        @BindView(R.id.task_CheckBox)
        CheckBox task_CheckBox;

        @BindView(R.id.divider_View)
        View divider_View;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface RecyclerClickListener {
        public void checkBoxClickListener(long taskId, String status);
    }
}
