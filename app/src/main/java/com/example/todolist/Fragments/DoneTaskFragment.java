package com.example.todolist.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todolist.Adapters.DoneAdpater;
import com.example.todolist.Entities.TaskDetailsEntity;
import com.example.todolist.R;

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

public class DoneTaskFragment extends Fragment implements DoneAdpater.RecyclerViewClickListener {
    @BindView(R.id.noDone_Constraint)
    ConstraintLayout noDone_Constraint;

    @BindView(R.id.doneRecyclerView)
    RecyclerView doneRecyclerView;

    DoneAdpater doneAdapter;
    TaskViewModel taskViewModel;

    public static DoneTaskFragment newInstance() {
        return new DoneTaskFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.done_task_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);

        doneRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        doneRecyclerView.setItemAnimator(new DefaultItemAnimator());
        doneAdapter = new DoneAdpater(getActivity(), this);
        doneRecyclerView.setAdapter(doneAdapter);

        getAllDoneTaskLiveData();
    }

    private void getAllDoneTaskLiveData() {
        taskViewModel.getAllDoneTaskLiveData("true").observe(getViewLifecycleOwner(), taskDetailsEntities -> {
            Timber.d("Done_Entities : %s", taskDetailsEntities.toString());
            updateUI(taskDetailsEntities);
        });
    }

    public void updateUI(List<TaskDetailsEntity> taskDetailsEntity) {
        if (taskDetailsEntity.size() == 0) {
            doneRecyclerView.setVisibility(View.GONE);
            noDone_Constraint.setVisibility(View.VISIBLE);
        } else {
            doneAdapter.setDoneTaskList(taskDetailsEntity);
            noDone_Constraint.setVisibility(View.GONE);
            doneRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void CheckBoxClickListener(long taskId, String status) {
        Timber.d("CheckBoxClickListener \n taskId : %d,  Status : %s", taskId, status);
        taskViewModel.updateTaskDoneStatus(taskId, status);
    }
}