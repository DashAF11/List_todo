package com.example.list_todo.Fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
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

    @BindView(R.id.task_SearchView)
    SearchView task_SearchView;

    @BindView(R.id.searchTask_TextView)
    TextView searchTask_TextView;
    @BindView(R.id.deleteAllTask_TextView)
    TextView deleteAllTask_TextView;
    @BindView(R.id.task_categoryName_TextView)
    TextView task_categoryName_TextView;
    @BindView(R.id.addTask_Empty_TextView)
    TextView addTask_Empty_TextView;

    @BindView(R.id.backTask_ImageView)
    ImageView backTask_ImageView;
    @BindView(R.id.addEmptyTask_ImageView)
    ImageView addEmptyTask_ImageView;
    @BindView(R.id.filter_ImageView)
    ImageView filter_ImageView;

    @BindView(R.id.searchTaskHider_Constraint)
    ConstraintLayout searchTaskHider_Constraint;
    @BindView(R.id.topTask_constraint)
    ConstraintLayout topTask_constraint;
    @BindView(R.id.empty_Constraint)
    ConstraintLayout empty_Constraint;
    @BindView(R.id.filter_Hider_Constraint)
    ConstraintLayout filter_Hider_Constraint;

    @BindView(R.id.delayedTask_CheckBox)
    CheckBox delayedTask_CheckBox;
    @BindView(R.id.doneTask_CheckBox)
    CheckBox doneTask_CheckBox;
    @BindView(R.id.priorityHigh_CheckBox)
    CheckBox priorityHigh_CheckBox;
    @BindView(R.id.priorityMed_CheckBox)
    CheckBox priorityMed_CheckBox;
    @BindView(R.id.priorityLow_CheckBox)
    CheckBox priorityLow_CheckBox;

    @BindView(R.id.taskRecyclerView)
    RecyclerView taskRecyclerView;

    TaskDetailsAdapter taskDetailsAdapter;
    TaskDetailsDao taskDetailsDao;
    NavController navController;
    NavOptions navOptions;
    TaskViewModel taskViewModel;
    long catId;
    String catName;
    Calendar calendar = Calendar.getInstance();
    long currentTimeStamp;
    boolean earlyTask, delayedTask, doneTask, highTask, medTask, lowTask;

    List<String> selectionList;


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
        calendar = Calendar.getInstance();
        currentTimeStamp = calendar.getTimeInMillis();

        if (getArguments() != null) {
            TaskFragmentArgs args = TaskFragmentArgs.fromBundle(getArguments());
            // Timber.d(" TaskFragmentArgs ==> catName : %s, CatId : %d", args.getCatName(), args.getCatId());
            catId = args.getCatId();
            catName = args.getCatName();

            task_categoryName_TextView.setText(catName);

            taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            new ItemTouchHelper(swipetoDeleteTask).attachToRecyclerView(taskRecyclerView);
            new ItemTouchHelper(swipetoEditTask).attachToRecyclerView(taskRecyclerView);
            taskRecyclerView.setItemAnimator(new DefaultItemAnimator());
            taskDetailsAdapter = new TaskDetailsAdapter(getActivity(), this);
            taskRecyclerView.setAdapter(taskDetailsAdapter);

            getAllTaskLiveData();
        }

        task_SearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Timber.d("catgory_SearchView : %s", newText);
                if (newText.length() == 0) {
                    return true;
                } else {
                    taskDetailsAdapter.getFilter().filter(newText);
                    taskDetailsAdapter.notifyDataSetChanged();
                    return false;
                }
            }
        });

        delayedTask_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                delayedTask = isChecked;
            }
        });

        doneTask_CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> doneTask = isChecked);

        priorityHigh_CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> highTask = isChecked);

        priorityMed_CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> medTask = isChecked);

        priorityLow_CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> lowTask = isChecked);
    }

    public void getAllTaskLiveData() {
        taskViewModel.getAllTaskLiveData(catId).observe(getViewLifecycleOwner(), taskDetailsEntities -> {
            // Timber.d("taskDetailsEntities : %s", taskDetailsEntities.toString());
            if (taskDetailsEntities.size() == 0) {
                filter_ImageView.setVisibility(View.GONE);
                topTask_constraint.setVisibility(View.GONE);
            } else {
                filter_ImageView.setVisibility(View.VISIBLE);
                topTask_constraint.setVisibility(View.VISIBLE);
            }
            updateUI(taskDetailsEntities, "Add Task", false);
        });
    }

    @OnClick(R.id.applyFilter_TextView)
    void sortedTaskLiveData() {
        getEveryThing();
    }

    public void getEveryThing() {

        selectionList = new ArrayList<>();

        if (delayedTask) { //0
            selectionList.add(String.valueOf(currentTimeStamp));
        } else {
            selectionList.add("0");
        }
        if (doneTask) { //1
            selectionList.add("true");
        } else {
            selectionList.add(null);
        }
        if (highTask) { //2
            selectionList.add("high");
        } else {
            selectionList.add(null);
        }
        if (medTask) { //3
            selectionList.add("med");
        } else {
            selectionList.add(null);
        }
        if (lowTask) {//4
            selectionList.add("low");
        } else {
            selectionList.add(null);
        }

        if (selectionList.size() != 0) {
            Timber.d("SelectionList  : %s", selectionList.toString());
            taskViewModel.getEveryThing(catId, Long.parseLong(selectionList.get(0)), selectionList.get(1), selectionList.get(2),
                    selectionList.get(3), String.valueOf(selectionList.get(4))).observe(getViewLifecycleOwner(), taskDetailsEntities -> {
                Timber.d("getEveryThing : Size : %d \nList : %s", taskDetailsEntities.size(), taskDetailsEntities.toString());
                updateUI(taskDetailsEntities, getString(R.string.task_not_found_filter), true);
            });
        }
        toggleView(filter_Hider_Constraint);
    }

    public void updateUI(List<TaskDetailsEntity> taskDetailsEntity, String message, boolean click) {
        if (taskDetailsEntity.size() == 0) {
            topTask_constraint.setVisibility(View.GONE);
            addTask_Empty_TextView.setText(message);
            taskRecyclerView.setVisibility(View.GONE);
            empty_Constraint.setVisibility(View.VISIBLE);
            if (click) {
                empty_Constraint.setClickable(false);
                addEmptyTask_ImageView.setVisibility(View.GONE);
            }
        } else {
            topTask_constraint.setVisibility(View.VISIBLE);
            taskDetailsAdapter.setTaskList(taskDetailsEntity);
            empty_Constraint.setVisibility(View.GONE);
            taskRecyclerView.setVisibility(View.VISIBLE);
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
            //  Timber.d("taskName : %s", taskName);

            deleteDialogBox(catId, taskId, taskName, "Delete " + taskName + "?", "Are you sure want to delete this task?", false);
        }
    };

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

    @OnClick(R.id.closeSort_ImageView)
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

    public void deleteDialogBox(long catId, long taskId, String taskName, String Title, String
            message, boolean allTask) {

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
                    dialogInterface.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), R.drawable.close_darkpurple_icon, (dialogInterface, which) -> {
                    FancyToast.makeText(getActivity(), getString(R.string.canceled), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    getAllTaskLiveData();
                    dialogInterface.dismiss();
                })
                .build();
        // Show Dialog
        mBottomSheetDialog.show();
    }

    @OnClick(R.id.searchTask_TextView)
    void searchTextClick() {
        toggleView(searchTaskHider_Constraint, searchTask_TextView, deleteAllTask_TextView);
    }

    @OnClick(R.id.deleteAllTask_TextView)
    void deleteAllCategories() {
        deleteDialogBox(0, 0, null, "Delete All Tasks?", "Are you sure want to delete all tasks?", true);
    }

    @OnClick(R.id.backTask_ImageView)
    void backClick() {
        toggleView(searchTaskHider_Constraint, searchTask_TextView, deleteAllTask_TextView);
        // getAllTaskLiveData();
    }

    void toggleView(ConstraintLayout constraintLayout, TextView textView1, TextView textView2) {
        if (constraintLayout.getVisibility() == View.VISIBLE) {
            constraintLayout.setVisibility(View.GONE);
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
        } else {
            constraintLayout.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.navigatebackTask_ImageView)
    void navigateBack() {
        navOptions = new NavOptions.Builder().setPopUpTo(R.id.dashboardFragment, true).build();
        navController.navigate(R.id.action_taskFragment_to_dashboardFragment, null, navOptions);
    }

    @OnClick(R.id.clearFilter_TextView)
    void clearSort() {
        getAllTaskLiveData();
        delayedTask_CheckBox.setChecked(false);
        doneTask_CheckBox.setChecked(false);
        priorityHigh_CheckBox.setChecked(false);
        priorityMed_CheckBox.setChecked(false);
        priorityLow_CheckBox.setChecked(false);
    }
}