package com.example.todolist.Fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.todolist.Adapters.CalendarViewAdapter;
import com.example.todolist.Entities.TaskDetailsEntity;
import com.example.todolist.ModelClasses.CalendarEvent;
import com.example.todolist.R;
import com.example.todolist.ViewModel.CalendarViewModel;
import com.example.todolist.ViewModel.TaskViewModel;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import timber.log.Timber;

public class CalendarViewFragment extends Fragment implements CalendarViewAdapter.TaskClickListener {

    @BindView(R.id.monthName_TextView)
    TextView monthName_TextView;

    @BindView(R.id.CalendarView)
    CompactCalendarView calendarView;

    @BindView(R.id.calendarRecyclerView)
    RecyclerView calendarRecyclerView;

    @BindView(R.id.empty_calendarConstraint)
    ConstraintLayout empty_calendarConstraint;

    CalendarViewAdapter calendarViewAdapter;
    SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMM-yyyy", Locale.getDefault());
    NavController navController;
    NavOptions navOptions;
    Date todaysDate, clickedDate;
    private SparseIntArray colorSparseIntArray;
    int pType_high = 0, pType_med = 1, pType_low = 2;
    List<CalendarEvent> calendarEventArrayList = new ArrayList<>();

    //swipe Left to delete_Task recyclerView
    ItemTouchHelper.SimpleCallback swipeEditTask = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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
    private CalendarViewModel calendarViewModel;
    private TaskViewModel taskViewModel;
    //swipe Left to delete_Task recyclerView
    ItemTouchHelper.SimpleCallback swipeDeleteTask = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
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
                    taskId = (long) viewHolder.itemView.getTag(R.id.taskId),
                    taskTimeStamp = (long) viewHolder.itemView.getTag(R.id.taskTimeStamp);
            ;
            String taskName = String.valueOf(viewHolder.itemView.getTag(R.id.taskName));
            //  Timber.d("taskName : %s", taskName);

            deleteDialogBox(viewHolder.getAdapterPosition(), catId, taskId, taskName, taskTimeStamp, "Delete " + taskName + "?", "Are you sure want to delete this task?", false);
        }
    };

    public static CalendarViewFragment newInstance() {
        return new CalendarViewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_view_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);

        todaysDate = Calendar.getInstance().getTime();
        navController = Navigation.findNavController(view);
        navOptions = new NavOptions.Builder().setPopUpTo(R.id.dashboardFragment, true).build();

        colorSparseIntArray = new SparseIntArray();
        colorSparseIntArray.append(pType_high, ContextCompat.getColor(getActivity(), R.color.red));
        colorSparseIntArray.append(pType_med, ContextCompat.getColor(getActivity(), R.color.orange));
        colorSparseIntArray.append(pType_low, ContextCompat.getColor(getActivity(), R.color.green));

        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.displayOtherMonthDays(false);
        calendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        monthName_TextView.setText(monthDateFormat.format(new Date()));

        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        new ItemTouchHelper(swipeDeleteTask).attachToRecyclerView(calendarRecyclerView);
        new ItemTouchHelper(swipeEditTask).attachToRecyclerView(calendarRecyclerView);
        calendarRecyclerView.setItemAnimator(new DefaultItemAnimator());
        calendarViewAdapter = new CalendarViewAdapter(getActivity(), this);
        calendarRecyclerView.setAdapter(calendarViewAdapter);

        getEvents();
        getTodaysTasks(todaysDate);

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date date) {
                clickedDate = date;
                Timber.d("ClickedDate : " + clickedDate);
                getTodaysTasks(date);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                calendarView.setCurrentDayBackgroundColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
                monthName_TextView.setText(monthDateFormat.format(firstDayOfNewMonth));
            }
        });
    }

    void getEvents() {

        calendarViewModel.getAllTask();
        calendarViewModel.getCalendarEvents().observe(getViewLifecycleOwner(), (List<CalendarEvent> calenderEvents) -> {
            Timber.d("calenderEvents : %d", calenderEvents.size());

            for (int i = 0; i < calenderEvents.size(); i++) {
                Timber.d("getTimestamp : %d", calenderEvents.get(i).getTimestamp());
            }
            calendarEventArrayList.addAll(calenderEvents);

            if (calenderEvents == null) return;
            for (CalendarEvent calendarEvents : calendarEventArrayList) {
                Event event = new Event(calendarEvents.getpTypeColor(), calendarEvents.getTimestamp(), calendarEvents);
                calendarView.addEvent(event);
            }
        });

        calendarEventArrayList.clear();

//        calendarViewModel.getCalendarEventLiveData().observe(getViewLifecycleOwner(), taskDetailsEntities -> {
//            List<CalendarEvent> allCalendarEvents = new ArrayList<>();
//            for (TaskDetailsEntity entity : taskDetailsEntities) {
//                CalendarEvent calendarEvent = new CalendarEvent();
//                calendarEvent.setTaskId(entity.getTask_id());
//                if (entity.getTask_priority().equals("high")) {
//                    calendarEvent.setpTypeColor(colorSparseIntArray.get(pType_high));
//                } else if (entity.getTask_priority().equals("med")) {
//                    calendarEvent.setpTypeColor(colorSparseIntArray.get(pType_med));
//                } else if (entity.getTask_priority().equals("low")) {
//                    calendarEvent.setpTypeColor(colorSparseIntArray.get(pType_low));
//                }
//                calendarEvent.setTimestamp(entity.getTimestamp());
//                allCalendarEvents.add(calendarEvent);
//                Timber.d("allCalendarEvents : %s", calendarEvent.toString());
//            }
//
//            if (allCalendarEvents == null) return;
//            for (CalendarEvent calendarEvents : allCalendarEvents) {
//                Event event = new Event(calendarEvents.getpTypeColor(), calendarEvents.getTimestamp(), calendarEvents);
//                calendarView.addEvent(event);
//            }

        //            allCalendarEvents.clear();

//        });
    }

    private void getTodaysTasks(Date todaysDate) {
        if (todaysDate == null) return;

        calendarViewModel.calendarTaskDetailsLiveData(todaysDate).observe(getViewLifecycleOwner(), taskDetailsEntities -> {
            if (taskDetailsEntities.size() == 0) {
                empty_calendarConstraint.setVisibility(View.VISIBLE);
                calendarRecyclerView.setVisibility(View.GONE);
            } else {
                empty_calendarConstraint.setVisibility(View.GONE);
                calendarRecyclerView.setVisibility(View.VISIBLE);
                calendarViewAdapter.setCalendarTaskList(taskDetailsEntities);
            }
        });
    }

    public void deleteDialogBox(int position, long catId, long taskId, String taskName, long taskTimeStamp,
                                String Title, String message, boolean allTask) {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())

                .setTitle(Title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.delete), R.drawable.delete_white_icon, (dialogInterface, which) -> {

                    if (allTask) {
                       // taskViewModel.deleteAllTasks();
                        FancyToast.makeText(getActivity(), getString(R.string.all_tasks_deleted), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    } else {
                        taskViewModel.deleteSingleTask(catId, taskId);
                        calendarViewAdapter.notifyItemRemoved(position);
                        FancyToast.makeText(getActivity(), taskName + " " + getString(R.string.deleted), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    }
                    Timber.d("Deleted taskTimeStamp : %d", taskTimeStamp);
                    //  calendarView.removeEvents(taskTimeStamp);
                   // calendarView.removeEvent(new Event(Color.WHITE, taskTimeStamp));
                    calendarView.removeEvents(taskTimeStamp);

                    //   getEvents();
                    dialogInterface.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), R.drawable.close_darkpurple_icon, (dialogInterface, which) -> {
                    FancyToast.makeText(getActivity(), getString(R.string.canceled), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    calendarViewAdapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                    calendarViewAdapter.notifyItemRangeChanged(position, calendarViewAdapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                    dialogInterface.dismiss();
                })
                .build();
        // Show Dialog
        mBottomSheetDialog.show();
    }

    public void editDialogBox(TaskDetailsEntity taskDetailsEntity) {
        addTask("Edit Task", taskDetailsEntity);
    }

    private void addTask(String edit_task, TaskDetailsEntity taskDetailsEntity) {
        CalendarViewFragmentDirections.ActionCalendarViewFragment2ToTaskAddEditFragment action = CalendarViewFragmentDirections.actionCalendarViewFragment2ToTaskAddEditFragment(taskDetailsEntity);
        action.setHeaderName(edit_task);
        action.setCatId(taskDetailsEntity.getTask_catID());
        action.setCatName(taskDetailsEntity.getTask_catName());
        action.setFromCalendar(true);

        navController.navigate(action);
    }

    @Override
    public void checkBoxClickListener(int position, long taskId, String status) {
        taskViewModel.updateTaskDoneStatus(taskId, status);
        taskViewModel.updateTaskStatus(taskId, status).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                Timber.d("updateTaskDoneStatus_onComplete");
                calendarViewAdapter.changeItem(position, status);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    @OnClick(R.id.backCalender_ImageView)
    void backClick() {
        navController.navigate(R.id.action_calendarViewFragment2_to_dashboardFragment, null, navOptions);
    }
}