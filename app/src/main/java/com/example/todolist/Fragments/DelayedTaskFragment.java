package com.example.todolist.Fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Adapters.DelayedAdapter;
import com.example.todolist.Entities.TaskDetailsEntity;
import com.example.todolist.R;
import com.example.todolist.ViewModel.TaskViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import timber.log.Timber;

public class DelayedTaskFragment extends Fragment {

    @BindView(R.id.noDelayed_Constraint)
    ConstraintLayout noDelayed_Constraint;

    @BindView(R.id.delayedRecyclerView)
    RecyclerView delayedRecyclerView;

    DelayedAdapter delayedAdapter;
    TaskViewModel taskViewModel;

    NavController navController;

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
        navController = Navigation.findNavController(view);

        delayedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        new ItemTouchHelper(swipetoDeleteTask).attachToRecyclerView(delayedRecyclerView);
        new ItemTouchHelper(swipetoEditTask).attachToRecyclerView(delayedRecyclerView);
        delayedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        delayedAdapter = new DelayedAdapter(getActivity());
        delayedRecyclerView.setAdapter(delayedAdapter);

        getAllDelayedTaskLiveData();
    }

    public void getAllDelayedTaskLiveData() {
        Calendar calendar = Calendar.getInstance();
        long currentTimeStamp = calendar.getTimeInMillis();
        Timber.d("currentTimeStamp : " + currentTimeStamp);

        taskViewModel.getAllDelayedTaskLiveData(currentTimeStamp, "false").observe(getViewLifecycleOwner(), taskDetailsEntities -> {
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

    //swipe Left to delete_Task recyclerView
    ItemTouchHelper.SimpleCallback swipetoEditTask = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.dark_purple_background))
                    .addActionIcon(R.drawable.edit_white_icon)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

            TaskDetailsEntity detailsEntity = (TaskDetailsEntity) viewHolder.itemView.getTag(R.id.taskDetails);
            // Timber.d("detailsEntity : %s", detailsEntity.toString());

            long catId = (long) viewHolder.itemView.getTag(R.id.catId);
            String catName = String.valueOf(viewHolder.itemView.getTag(R.id.taskName));

            addTask("Edit Task", detailsEntity, catId, catName);

        }
    };

    private void addTask(String header, TaskDetailsEntity detailsEntity, long catId, String catName) {
        DashboardFragmentDirections.ActionDashboardFragmentToTaskAddEditFragment action = DashboardFragmentDirections.actionDashboardFragmentToTaskAddEditFragment(detailsEntity);
        action.setHeaderName(header);
        action.setCatId(catId);
        action.setCatName(catName);
        action.setFromCalendar(false);
        action.setFromDelayed(true);
        navController.navigate(action);
    }

    //swipe Left to delete_Task recyclerView
    ItemTouchHelper.SimpleCallback swipetoDeleteTask = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.dark_purple_background))
                    .addActionIcon(R.drawable.delete_white_icon)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

            long catId = (long) viewHolder.itemView.getTag(R.id.catId),
                    taskId = (long) viewHolder.itemView.getTag(R.id.taskId);
            String taskName = String.valueOf(viewHolder.itemView.getTag(R.id.taskName));
            //  Timber.d("taskName : %s", taskName);

            deleteDialogBox(viewHolder.getAdapterPosition(), catId, taskId, taskName, "Delete " + taskName + "?", "Are you sure want to delete this task?", false);
        }
    };

    public void deleteDialogBox(int position, long catId, long taskId, String taskName, String Title, String message, boolean allTask) {

        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                .setTitle(Title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.delete), R.drawable.delete_white_icon, (dialogInterface, which) -> {
                    if (allTask) {
                        taskViewModel.deleteAllTasks();
                        FancyToast.makeText(getActivity(), getString(R.string.all_tasks_deleted), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    } else {
                        taskViewModel.deleteSingleTask(catId, taskId);
                        FancyToast.makeText(getActivity(), taskName + " " + getString(R.string.deleted), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    }
                    delayedAdapter.notifyItemRemoved(position);
                    //getAllTasksData();
                    dialogInterface.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), R.drawable.close_darkpurple_icon, (dialogInterface, which) -> {
                    FancyToast.makeText(getActivity(), getString(R.string.canceled), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    delayedAdapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                    delayedAdapter.notifyItemRangeChanged(position, delayedAdapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                    dialogInterface.dismiss();
                })
                .build();
        // Show Dialog
        mBottomSheetDialog.show();
    }

}