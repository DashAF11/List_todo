package com.example.todolist.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.todolist.Entities.TaskDetailsEntity;
import com.example.todolist.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.graphics.Color.rgb;

public class DoneAdpater extends RecyclerView.Adapter<DoneAdpater.ViewHolder> {
    Context context;
    List<TaskDetailsEntity> taskDetailsEntities = new ArrayList<>();
    RecyclerViewClickListener clickListener;

    public DoneAdpater(Context context, RecyclerViewClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    public void setDoneTaskList(List<TaskDetailsEntity> doneTaskList) {
        this.taskDetailsEntities = doneTaskList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.done_tasks_recycler_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskDetailsEntity entity = taskDetailsEntities.get(position);

        holder.doneTaskName_TextView.setText(entity.getTask_name());
        holder.doneTaskCategory_TextView.setText(entity.getTask_catName());

        holder.doneTaskTime_TextView.setText(entity.getTask_time());

        boolean today = DateUtils.isToday(taskDetailsEntities.get(position).getTimestamp());
        Timber.d("isToday : " + today);
        if (today) {
            holder.doneTaskDate_TextView.setText("Today");
        } else {
            holder.doneTaskDate_TextView.setText(entity.getTask_date());
        }

        if (taskDetailsEntities.get(position).getTask_done_status().equals("true")) {
            holder.doneTask_CheckBox.setChecked(true);
        } else {
            holder.doneTask_CheckBox.setChecked(false);
        }

        holder.doneTask_CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                clickListener.CheckBoxClickListener(taskDetailsEntities.get(position).getTask_id(), "false");
            }
        });

        if (taskDetailsEntities.get(position).getTask_alarm().equals("true")) {
            holder.alarm_done_ImageView.setVisibility(View.VISIBLE);
        } else {
            holder.alarm_done_ImageView.setVisibility(View.GONE);
        }

        if (taskDetailsEntities.get(position).getTask_priority().equals("high")) {
            holder.priorityColor_donTask_View.setBackgroundColor(rgb(230, 0, 0));
        } else if (taskDetailsEntities.get(position).getTask_priority().equals("med")) {
            holder.priorityColor_donTask_View.setBackgroundColor(rgb(255, 128, 0));
        } else if (taskDetailsEntities.get(position).getTask_priority().equals("low")) {
            holder.priorityColor_donTask_View.setBackgroundColor(rgb(0, 128, 0));
        }


    }

    @Override
    public int getItemCount() {
        return taskDetailsEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.doneTaskName_TextView)
        TextView doneTaskName_TextView;
        @BindView(R.id.doneTaskCategory_TextView)
        TextView doneTaskCategory_TextView;
        @BindView(R.id.doneTaskDate_TextView)
        TextView doneTaskDate_TextView;
        @BindView(R.id.doneTaskTime_TextView)
        TextView doneTaskTime_TextView;

        @BindView(R.id.priorityColor_donTask_View)
        ConstraintLayout priorityColor_donTask_View;

        @BindView(R.id.alarm_done_ImageView)
        ImageView alarm_done_ImageView;

        @BindView(R.id.done_divider_View)
        View done_divider_View;

        @BindView(R.id.doneTask_CheckBox)
        CheckBox doneTask_CheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface RecyclerViewClickListener {
        public void CheckBoxClickListener(long taskId, String status);
    }
}
