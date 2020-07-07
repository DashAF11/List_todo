package com.example.list_todo.Fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.list_todo.Adapters.TaskDetailsAdapter;
import com.example.list_todo.Dao.TaskDetailsDao;
import com.example.list_todo.Entities.TaskDetailsEntity;
import com.example.list_todo.R;
import com.example.list_todo.RoomDB.RoomDB;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.Calendar;
import java.util.List;

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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import timber.log.Timber;

public class TaskFragment extends Fragment implements TaskDetailsAdapter.RecyclerClickListener {

    @BindView(R.id.task_categoryName_TextView)
    TextView task_categoryName_TextView;
    @BindView(R.id.addTask_Empty_TextView)
    TextView addTask_Empty_TextView;

    @BindView(R.id.addEmptyTask_ImageView)
    ImageView addEmptyTask_ImageView;
    @BindView(R.id.filter_ImageView)
    ImageView filter_ImageView;

    @BindView(R.id.empty_Constraint)
    ConstraintLayout empty_Constraint;
    @BindView(R.id.filter_Hider_Constraint)
    ConstraintLayout filter_Hider_Constraint;

    @BindView(R.id.filter_radioGroup)
    RadioGroup filter_radioGroup;
    @BindView(R.id.allTask_RadioButton)
    RadioButton allTask_RadioButton;
    @BindView(R.id.datewiseTask_RadioButton)
    RadioButton datewiseTask_RadioButton;
    @BindView(R.id.alphabeticallyTask_RadioButton)
    RadioButton alphabeticallyTask_RadioButton;
    @BindView(R.id.doneTask_RadioButton)
    RadioButton doneTask_RadioButton;
    @BindView(R.id.priorityHigh_RadioButton)
    RadioButton priorityHigh_RadioButton;
    @BindView(R.id.priorityMed_RadioButton)
    RadioButton priorityMed_RadioButton;
    @BindView(R.id.priorityLow_RadioButton)
    RadioButton priorityLow_RadioButton;

    @BindView(R.id.taskRecyclerView)
    RecyclerView taskRecyclerView;

    TaskDetailsAdapter taskDetailsAdapter;
    TaskDetailsDao taskDetailsDao;
    NavController navController;
    TaskViewModel taskViewModel;
    long catId;
    String catName;
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
            Timber.d("detailsEntity : %s", detailsEntity.toString());

            editDialogBox(detailsEntity);
        }
    };
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
            Timber.d("taskName : %s", taskName);

            deleteDialogBox(catId, taskId, taskName);
        }
    };

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        navController = Navigation.findNavController(view);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        taskDetailsDao = RoomDB.getRoomDB(getActivity()).taskDetailsDao();
//        spfUser = getActivity().getSharedPreferences(StorageConstants.KEY_CATEGORYDETAILS, Context.MODE_PRIVATE);
//
//        catId = spfUser.getLong(Keys.KEY_CATID, Long.parseLong("0"));
//        catName = spfUser.getString(KEY_CATNAME, "Category");
//        Timber.d("SharedPref \ncatId : %d, catName : %s", catId, catName);

        if (getArguments() != null) {
            TaskFragmentArgs args = TaskFragmentArgs.fromBundle(getArguments());
            Timber.d(" TaskFragmentArgs ==> catName : %s, CatId : %d", args.getCatName(), args.getCatId());
            catId = args.getCatId();
            catName = args.getCatName();

            task_categoryName_TextView.setText(catName);

            taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            new ItemTouchHelper(swipetoDeleteTask).attachToRecyclerView(taskRecyclerView);
            new ItemTouchHelper(swipetoEditTask).attachToRecyclerView(taskRecyclerView);
            taskRecyclerView.setItemAnimator(new DefaultItemAnimator());
            taskDetailsAdapter = new TaskDetailsAdapter(getActivity(), this);
            taskRecyclerView.setAdapter(taskDetailsAdapter);

            allTask_RadioButton.setChecked(true);
            getAllTaskLiveData();

            filter_radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.allTask_RadioButton) {
                    getAllTaskLiveData();
                    toggleView(filter_Hider_Constraint);
                    FancyToast.makeText(getActivity(), "All Tasks", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                } else if (checkedId == R.id.datewiseTask_RadioButton) {
                    getAllTaskTimeStampWiseLiveData();
                    toggleView(filter_Hider_Constraint);
                    FancyToast.makeText(getActivity(), "Early Tasks", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                } else if (checkedId == R.id.alphabeticallyTask_RadioButton) {
                    getAllTaskAlphabeticallyLiveData();
                    toggleView(filter_Hider_Constraint);
                    FancyToast.makeText(getActivity(), "Alphabetically", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                } else if (checkedId == R.id.delayedTask_RadioButton) {
                    getAllDelayedTaskLiveData();
                    toggleView(filter_Hider_Constraint);
                    FancyToast.makeText(getActivity(), "Delayed Tasks", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                } else if (checkedId == R.id.doneTask_RadioButton) {
                    getAllDoneTaskLiveData();
                    toggleView(filter_Hider_Constraint);
                    FancyToast.makeText(getActivity(), "Done Tasks", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                } else if (checkedId == R.id.priorityHigh_RadioButton) {
                    getAllTaskPriorityWiseLiveData("high");
                    toggleView(filter_Hider_Constraint);
                    FancyToast.makeText(getActivity(), "High Priority Tasks", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                } else if (checkedId == R.id.priorityMed_RadioButton) {
                    getAllTaskPriorityWiseLiveData("med");
                    toggleView(filter_Hider_Constraint);
                    FancyToast.makeText(getActivity(), "Medium Priority Tasks", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                } else if (checkedId == R.id.priorityLow_RadioButton) {
                    getAllTaskPriorityWiseLiveData("low");
                    toggleView(filter_Hider_Constraint);
                    FancyToast.makeText(getActivity(), "Low Priority Tasks", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                }
            });
        }
    }

    public void getAllTaskLiveData() {
        taskViewModel.getAllTaskLiveData(catId).observe(getViewLifecycleOwner(), taskDetailsEntities -> {
            Timber.d("taskDetailsEntities : %s", taskDetailsEntities.toString());
            if (taskDetailsEntities.size() == 0) {
                filter_ImageView.setVisibility(View.GONE);
            } else {
                filter_ImageView.setVisibility(View.VISIBLE);
            }
            updateUI(taskDetailsEntities, "Add Task");
        });
    }

    public void getAllDelayedTaskLiveData() {
        Calendar calendar = Calendar.getInstance();
        long currentTimeStamp = calendar.getTimeInMillis();
        Timber.d("currentTimeStamp : " + currentTimeStamp);

        taskViewModel.getAllDelayTask_byCategoryLiveData(currentTimeStamp, catId).observe(getViewLifecycleOwner(), taskDetailsEntities -> {
            Timber.d("Delayed_Entities : %s", taskDetailsEntities.toString());
            updateUI(taskDetailsEntities, "Tasks are not delayed yet!");
        });
    }

    public void getAllDoneTaskLiveData() {
        taskViewModel.getAllDoneTask_byCategoryLiveData("true", catId).observe(getViewLifecycleOwner(), taskDetailsEntities -> {
            Timber.d("Done_Entities : %s", taskDetailsEntities.toString());
            updateUI(taskDetailsEntities, "Tasks are not done yet!");
        });
    }

    public void getAllTaskAlphabeticallyLiveData() {
        taskViewModel.getAllTaskAlphabetically_byCategoryLiveData(catId).observe(getViewLifecycleOwner(), taskDetailsEntities -> {
            Timber.d("Alphabetically_Entities : %s", taskDetailsEntities.toString());
            updateUI(taskDetailsEntities, "Add Task");
        });
    }

    public void getAllTaskTimeStampWiseLiveData() {
        taskViewModel.getAllTaskTimeStampWise_byCategoryLiveData(catId).observe(getViewLifecycleOwner(), taskDetailsEntities -> {
            Timber.d("Timestamp_Entities : %s", taskDetailsEntities.toString());
            updateUI(taskDetailsEntities, "Add Task");
        });
    }

    public void getAllTaskPriorityWiseLiveData(String priority) {
        taskViewModel.getAllTaskPriorityWiseLiveData(priority, catId).observe(getViewLifecycleOwner(), taskDetailsEntities -> {
            Timber.d("Priority_Entities : %s", taskDetailsEntities.toString());
            updateUI(taskDetailsEntities, "No task as priority " + priority);
        });
    }

    public void updateUI(List<TaskDetailsEntity> taskDetailsEntity, String message) {
        if (taskDetailsEntity.size() == 0) {
            taskRecyclerView.setVisibility(View.GONE);
            empty_Constraint.setVisibility(View.VISIBLE);
            addTask_Empty_TextView.setText(message);
        } else {
            taskDetailsAdapter.setTaskList(taskDetailsEntity);
            empty_Constraint.setVisibility(View.GONE);
            taskRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    void toggleView(ConstraintLayout constraintLayout) {
        if (constraintLayout.getVisibility() == View.VISIBLE) {
            constraintLayout.setVisibility(View.GONE);
        } else {
            constraintLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.filter_ImageView)
    public void filterClick() {
        toggleView(filter_Hider_Constraint);
    }

    @OnClick(R.id.closeFilter_ImageView)
    public void closeFilter() {
        toggleView(filter_Hider_Constraint);
    }

    @OnClick(R.id.empty_Constraint)
    public void empty_ConstraintClick() {
        addTask("Add Task", null);
    }

    @OnClick(R.id.addTask_ImageView)
    public void addTask_IconClick() {
        addTask("Add Task", null);
    }

    void addTask(String header, TaskDetailsEntity taskDetailsEntity) {
        TaskFragmentDirections.ActionTaskFragmentToTaskAddEditFragment action = TaskFragmentDirections.actionTaskFragmentToTaskAddEditFragment(taskDetailsEntity);
        action.setHeaderName(header);
        action.setCatId(catId);
        action.setCatName(catName);
        navController.navigate(action);
    }

    @Override
    public void checkBoxClickListener(long taskId, String status) {
        //  Timber.d("checkBoxClickListener : %d  %s", taskId, status);
        taskViewModel.updateTaskDoneStatus(taskId, status);
    }

    public void editDialogBox(TaskDetailsEntity taskDetailsEntity) {
        addTask("Edit Task", taskDetailsEntity);
    }

    public void deleteDialogBox(long catId, long taskId, String taskName) {

        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())

                .setTitle("Delete " + taskName + " ?")
                .setMessage("Are you sure want to delete this task?")
                .setCancelable(false)
                .setPositiveButton("Delete", R.drawable.delete_white_icon, (dialogInterface, which) -> {
                    FancyToast.makeText(getActivity(), taskName + " " + getString(R.string.deleted), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();

                    taskViewModel.deleteSingleTask(catId, taskId);

                    dialogInterface.dismiss();
                })
                .setNegativeButton("Cancel", R.drawable.close_darkpurple_icon, (dialogInterface, which) -> {
                    FancyToast.makeText(getActivity(), getString(R.string.canceled), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    getAllTaskLiveData();
                    dialogInterface.dismiss();
                })
                .build();
        // Show Dialog
        mBottomSheetDialog.show();
    }
}