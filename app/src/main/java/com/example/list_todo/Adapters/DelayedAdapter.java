package com.example.list_todo.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.list_todo.Entities.TaskDetailsEntity;
import com.example.list_todo.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.graphics.Color.rgb;

public class DelayedAdapter extends RecyclerView.Adapter<DelayedAdapter.ViewHolder> {

    Context context;
    List<TaskDetailsEntity> taskDetailsEntity = new ArrayList<>();
    ;

    public DelayedAdapter(Context context) {
        this.context = context;
    }

    public void setDelayedList(List<TaskDetailsEntity> taskDetailsEntity) {
        this.taskDetailsEntity = taskDetailsEntity;
        Timber.d("taskDetailsEntity_Size : %s", taskDetailsEntity.size());
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.delayed_tasks_recycler_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskDetailsEntity entity = taskDetailsEntity.get(position);

        holder.delayedTaskName_TextView.setText(entity.getTask_name());
        holder.delayedTaskCategory_TextView.setText(entity.getTask_catName());

        holder.delayedTaskTime_TextView.setText(entity.getTask_time());

        boolean today = DateUtils.isToday(taskDetailsEntity.get(position).getTimestamp());
        Timber.d("isToday : " + today);
        if (today) {
            holder.delayedTaskDate_TextView.setText("Today");
        } else {
            holder.delayedTaskDate_TextView.setText(entity.getTask_date());
        }

        if (taskDetailsEntity.get(position).getTask_done_status().equals("true")) {
            holder.delayed_done_ImageView.setVisibility(View.VISIBLE);
        } else {
            holder.delayed_done_ImageView.setVisibility(View.GONE);
        }

        if (taskDetailsEntity.get(position).getTask_alarm().equals("true")) {
            holder.alarm_delayed_ImageView.setVisibility(View.VISIBLE);
        } else {
            holder.alarm_delayed_ImageView.setVisibility(View.GONE);
        }

        if (taskDetailsEntity.get(position).getTask_priority().equals("high")) {
            holder.priorityColor_donTask_View.setBackgroundColor(rgb(230, 0, 0));
        } else if (taskDetailsEntity.get(position).getTask_priority().equals("med")) {
            holder.priorityColor_donTask_View.setBackgroundColor(rgb(255, 128, 0));
        } else if (taskDetailsEntity.get(position).getTask_priority().equals("low")) {
            holder.priorityColor_donTask_View.setBackgroundColor(rgb(0, 128, 0));
        }

    }

    @Override
    public int getItemCount() {
        return taskDetailsEntity.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.delayedTaskName_TextView)
        TextView delayedTaskName_TextView;
        @BindView(R.id.delayedTaskCategory_TextView)
        TextView delayedTaskCategory_TextView;
        @BindView(R.id.delayedTaskDate_TextView)
        TextView delayedTaskDate_TextView;
        @BindView(R.id.delayedTaskTime_TextView)
        TextView delayedTaskTime_TextView;

        @BindView(R.id.priorityColor_donTask_View)
        ConstraintLayout priorityColor_donTask_View;

        @BindView(R.id.delayed_done_ImageView)
        ImageView delayed_done_ImageView;
        @BindView(R.id.alarm_delayed_ImageView)
        ImageView alarm_delayed_ImageView;

        @BindView(R.id.delayed_divider_View)
        View delayed_divider_View;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
