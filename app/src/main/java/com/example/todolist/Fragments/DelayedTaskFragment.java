package com.example.todolist.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todolist.Adapters.DelayedAdapter;
import com.example.todolist.Entities.TaskDetailsEntity;
import com.example.todolist.R;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DelayedTaskFragment extends Fragment {

    @BindView(R.id.noDelayed_Constraint)
    ConstraintLayout noDelayed_Constraint;

    @BindView(R.id.delayedRecyclerView)
    RecyclerView delayedRecyclerView;

    DelayedAdapter delayedAdapter;
    TaskViewModel taskViewModel;

    public static DelayedTaskFragment newInstance() {
        return new DelayedTaskFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.delayed_task_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);

        delayedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        new ItemTouchHelper(swipetoDeleteTask).attachToRecyclerView(delayedRecyclerView);
//        new ItemTouchHelper(swipetoEditTask).attachToRecyclerView(delayedRecyclerView);
        delayedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        delayedAdapter = new DelayedAdapter(getActivity());
        delayedRecyclerView.setAdapter(delayedAdapter);

        getAllDelayedTaskLiveData();
    }

    public void getAllDelayedTaskLiveData() {
        Calendar calendar = Calendar.getInstance();
        long currentTimeStamp = calendar.getTimeInMillis();
        Timber.d("currentTimeStamp : " + currentTimeStamp);

        taskViewModel.getAllDelayedTaskLiveData(currentTimeStamp).observe(getViewLifecycleOwner(), taskDetailsEntities -> {
            Timber.d("Delayed_Entities : %s", taskDetailsEntities.toString());
            updateUI(taskDetailsEntities);
        });
    }

    public void updateUI(List<TaskDetailsEntity> taskDetailsEntity) {
        if (taskDetailsEntity.size() == 0) {
            delayedRecyclerView.setVisibility(View.GONE);
            noDelayed_Constraint.setVisibility(View.VISIBLE);
        } else {
            delayedAdapter.setDelayedList(taskDetailsEntity);
            noDelayed_Constraint.setVisibility(View.GONE);
            delayedRecyclerView.setVisibility(View.VISIBLE);
        }
    }

}