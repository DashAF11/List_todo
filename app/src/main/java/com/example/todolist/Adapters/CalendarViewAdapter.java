package com.example.todolist.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Entities.TaskDetailsEntity;
import com.example.todolist.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.graphics.Color.rgb;

public class CalendarViewAdapter extends RecyclerView.Adapter<CalendarViewAdapter.ViewHolder> {

    Context context;
    Calendar calendar = Calendar.getInstance();
    List<TaskDetailsEntity> taskDetailsEntities = new ArrayList<>();
    TaskClickListener taskClickListener;

    public CalendarViewAdapter(Context context, TaskClickListener taskClickListener) {
        this.context = context;
        this.taskClickListener = taskClickListener;
    }

    public void setCalendarTaskList(List<TaskDetailsEntity> taskList) {
        this.taskDetailsEntities = taskList;
        Timber.d("taskDetailsEntities : %d", taskDetailsEntities.size());
        this.notifyDataSetChanged();
    }

    public void changeItem(int position, String status) {
        taskDetailsEntities.get(position).setTask_done_status(status);
        this.notifyItemChanged(position);
    }

    public void changeDeleteItem(int position) {
        this.notifyItemChanged(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.calendar_recyclerview_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskDetailsEntity taskDetailsEntity = taskDetailsEntities.get(position);

        holder.calendar_taskName_TextView.setText(taskDetailsEntity.getTask_name());
        holder.calendar_categoryName_TextView.setText(taskDetailsEntity.getTask_catName());
        holder.calendar_taskID_TextView.setText("" + taskDetailsEntity.getTask_id());
        holder.calendar_taskDate_TextView.setText(taskDetailsEntity.getTask_date());
        holder.calendar_taskTime_TextView.setText(taskDetailsEntity.getTask_time());

        if (taskDetailsEntity.getTask_date().equals("false")) {
            holder.calendar_taskDate_TextView.setVisibility(View.GONE);
            holder.calendar_taskTime_TextView.setVisibility(View.GONE);
        } else {
            boolean today = DateUtils.isToday(taskDetailsEntity.getTimestamp());
            //Timber.d("isToday : " + today);
            if (today) {
                holder.calendar_taskDate_TextView.setText("Today");
            } else {
                holder.calendar_taskDate_TextView.setText(taskDetailsEntity.getTask_date());
            }
        }

        if (taskDetailsEntity.getTask_alarm().equals("true")) {
            holder.calendar_alarmTask_ImageView.setVisibility(View.VISIBLE);
        } else {
            holder.calendar_alarmTask_ImageView.setVisibility(View.GONE);
        }

        if (taskDetailsEntity.getTimestamp() <= calendar.getTimeInMillis()) {
            holder.calendar_delayTask_ImageView.setVisibility(View.VISIBLE);
        } else {
            holder.calendar_delayTask_ImageView.setVisibility(View.GONE);
        }

        switch (taskDetailsEntity.getTask_priority()) {
            case "high":
                holder.calendar_priority_color_View.setBackgroundColor(rgb(230, 0, 0));
                break;
            case "med":
                holder.calendar_priority_color_View.setBackgroundColor(rgb(255, 128, 0));
                break;
            case "low":
                holder.calendar_priority_color_View.setBackgroundColor(rgb(0, 128, 0));
                break;
        }

        if (taskDetailsEntity.getTask_done_status().equals("true")) {
            holder.calendar_task_CheckBox.setChecked(true);
            holder.calendar_taskName_TextView.setPaintFlags(holder.calendar_taskName_TextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.calendar_task_CheckBox.setChecked(false);
            holder.calendar_taskName_TextView.setPaintFlags(holder.calendar_taskName_TextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.calendar_task_CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                taskClickListener.checkBoxClickListener(position, taskDetailsEntity.getTask_id(), "true");
            } else {
                taskClickListener.checkBoxClickListener(position, taskDetailsEntity.getTask_id(), "false");
            }
        });

        holder.itemView.setTag(R.id.catId, taskDetailsEntity.getTask_catID());
        holder.itemView.setTag(R.id.taskId, taskDetailsEntity.getTask_id());
        holder.itemView.setTag(R.id.taskName, taskDetailsEntity.getTask_name());
        holder.itemView.setTag(R.id.taskTimeStamp, taskDetailsEntity.getTimestamp());
        holder.itemView.setTag(R.id.taskDetails, taskDetailsEntity);
    }

    @Override
    public int getItemCount() {
        return taskDetailsEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.calendar_taskID_TextView)
        TextView calendar_taskID_TextView;
        @BindView(R.id.calendar_taskName_TextView)
        TextView calendar_taskName_TextView;
        @BindView(R.id.calendar_categoryName_TextView)
        TextView calendar_categoryName_TextView;
        @BindView(R.id.calendar_taskDate_TextView)
        TextView calendar_taskDate_TextView;
        @BindView(R.id.calendar_taskTime_TextView)
        TextView calendar_taskTime_TextView;

        @BindView(R.id.calendar_delayTask_ImageView)
        ImageView calendar_delayTask_ImageView;
        @BindView(R.id.calendar_alarmTask_ImageView)
        ImageView calendar_alarmTask_ImageView;

        @BindView(R.id.calendar_priority_color_View)
        ConstraintLayout calendar_priority_color_View;

        @BindView(R.id.calendar_task_CheckBox)
        CheckBox calendar_task_CheckBox;

        @BindView(R.id.calendar_divider_View)
        View calendar_divider_View;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface TaskClickListener {
        public void checkBoxClickListener(int position, long taskId, String status);
    }
}
