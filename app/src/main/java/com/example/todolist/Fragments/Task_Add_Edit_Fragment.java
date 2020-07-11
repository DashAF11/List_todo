package com.example.todolist.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.todolist.Entities.TaskDetailsEntity;
import com.example.todolist.R;
import com.example.todolist.ViewModel.TaskViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class Task_Add_Edit_Fragment extends Fragment {

    @BindView(R.id.headerTask_TextView)
    TextView headerTask_TextView;
    @BindView(R.id.dateValue_TextView)
    TextView dateValue_TextView;
    @BindView(R.id.timeValue_TextView)
    TextView timeValue_TextView;
    @BindView(R.id.priorityValue_TextView)
    TextView priorityValue_TextView;

    @BindView(R.id.priorityHider_Constraint)
    ConstraintLayout priorityHider_Constraint;

    @BindView(R.id.priorityArrow_ImageView)
    ImageView priorityArrow_ImageView;

    @BindView(R.id.taskName_EditText)
    EditText taskName_EditText;

    @BindView(R.id.alarm_Switch)
    SwitchCompat alarm_Switch;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.high_RadioButton)
    RadioButton high_RadioButton;
    @BindView(R.id.med_RadioButton)
    RadioButton med_RadioButton;
    @BindView(R.id.low_RadioButton)
    RadioButton low_RadioButton;

    String headerText, catName, taskName, taskDate, taskTime, taskPriority = "low",
            taskAlarm = "false", mytimeHR_M, hr_forTS, minute_forTS, AM_PM;
    long taskTimeStamp, catId, taskId;
    int mYear, mMonth, mDay, mHour, mMinute, day_forTS, month_forTS, year_forTS;
    boolean editTextClick = false, checkEditText_Click, fromCalendar;
    TaskViewModel taskViewModel;
    NavController navController;
    NavOptions navOptions, navOptions2;
    TaskDetailsEntity taskDetailsEntity = new TaskDetailsEntity();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task__add__edit_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        low_RadioButton.setChecked(true);
        if (getArguments() != null) {
            Task_Add_Edit_FragmentArgs args = Task_Add_Edit_FragmentArgs.fromBundle(getArguments());
            headerText = args.getHeaderName();
            catId = args.getCatId();
            catName = args.getCatName();
            fromCalendar = args.getFromCalendar();
            headerTask_TextView.setText(headerText);

            TaskDetailsEntity taskDetailsEntity = args.getTaskDetails();
//            Timber.d("taskDetailsEntity : %s", taskDetailsEntity.toString());

            if (headerText.equals("Edit Task")) {
                taskId = taskDetailsEntity.getTask_id();
                taskName_EditText.setText(taskDetailsEntity.getTask_name());
                taskName = taskDetailsEntity.getTask_name();
                taskAlarm = taskDetailsEntity.getTask_alarm();
                if (taskDetailsEntity.getTask_date().equals("false")) {
                    dateValue_TextView.setText("--/--/----");
                    timeValue_TextView.setText("--:-- AM/PM");
                } else {
                    dateValue_TextView.setText(taskDetailsEntity.getTask_date());
                    timeValue_TextView.setText(taskDetailsEntity.getTask_time());
                }


                if (!taskDetailsEntity.getTask_date().equals("false")) {
                    taskDate = taskDetailsEntity.getTask_date();
                    taskTime = taskDetailsEntity.getTask_time();
                    String time, hour, minutes, am_pm;
                    time = taskTime;

                    hour = time.substring(0, 2);
                    hr_forTS = hour;
                    minutes = time.substring(3, 5);
                    minute_forTS = minutes;

                    am_pm = time.substring(6, 8);
                    AM_PM = am_pm;
                    Timber.d("hour : %s , Min : %s,  AM_PM : %s", hour, minutes, am_pm);
                }


                if (taskDetailsEntity.getTask_alarm().equals("true")) {
                    alarm_Switch.setChecked(true);
                    taskAlarm = "true";
                } else {
                    alarm_Switch.setChecked(false);
                    taskAlarm = "false";
                }

                if (taskDetailsEntity.getTask_priority().equals("high")) {
                    priorityValue_TextView.setText("High");
                    taskPriority = "high";
                    high_RadioButton.setChecked(true);
                } else if (taskDetailsEntity.getTask_priority().equals("med")) {
                    priorityValue_TextView.setText("Medium");
                    taskPriority = "med";
                    med_RadioButton.setChecked(true);
                } else if (taskDetailsEntity.getTask_priority().equals("low")) {
                    priorityValue_TextView.setText("Low");
                    taskPriority = "low";
                    low_RadioButton.setChecked(true);
                }
            }
        }

        navController = Navigation.findNavController(view);
        navOptions = new NavOptions.Builder().setPopUpTo(R.id.taskFragment, true).build();
        navOptions2 = new NavOptions.Builder().setPopUpTo(R.id.calendarViewFragment2, true).build();

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);

        taskName_EditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        taskName_EditText.setSingleLine(true);

        taskName_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEditText_Click = true;
            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.high_RadioButton) {
                taskPriority = "high";
                priorityValue_TextView.setText("High");
                toggleView(priorityHider_Constraint, priorityArrow_ImageView);
            } else if (checkedId == R.id.med_RadioButton) {
                taskPriority = "med";
                priorityValue_TextView.setText("Medium");
                toggleView(priorityHider_Constraint, priorityArrow_ImageView);
            } else if (checkedId == R.id.low_RadioButton) {
                taskPriority = "low";
                priorityValue_TextView.setText("Low");
                toggleView(priorityHider_Constraint, priorityArrow_ImageView);
            }
        });

        alarm_Switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                taskAlarm = "true";
            } else {
                taskAlarm = "false";
            }
        });

        taskName_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextClick = true;
            }
        });
    }

    @OnClick(R.id.priority_Constraint)
    public void priority_ArrowClick() {
        toggleView(priorityHider_Constraint, priorityArrow_ImageView);
    }

    @OnClick(R.id.date_Constraint)
    public void dateClick() {
        if (checkEditText_Click) {
            hideSoftKeyboard(getActivity());
        }
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                String mon, day;
                if (monthOfYear < 10) {
                    mon = "0" + (monthOfYear + 1);
                } else {
                    mon = "" + (monthOfYear + 1);
                }
                if (dayOfMonth < 10) {
                    day = "0" + dayOfMonth;
                } else {
                    day = "" + dayOfMonth;
                }
                day_forTS = Integer.parseInt(day);
                month_forTS = Integer.parseInt(mon);
                year_forTS = year;
                taskDate = day + "-" + mon + "-" + year;
                dateValue_TextView.setText(day + "-" + mon + "-" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @OnClick(R.id.time_Constraint)
    public void timeClick() {
        if (checkEditText_Click) {
            hideSoftKeyboard(getActivity());
        }

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hrs;
                String min;
                if (hourOfDay < 10) {
                    hrs = "0" + hourOfDay;
                } else {
                    hrs = "" + hourOfDay;
                }
                if (minute < 10) {
                    min = "0" + minute;
                } else {
                    min = "" + minute;
                }
                mytimeHR_M = (hrs + ":" + min);

                AM_PM = " AM";
                String mm_precede = "";
                String hr_precede = "";
                if (hourOfDay >= 12) {
                    AM_PM = " PM";
                    if (hourOfDay >= 13 && hourOfDay < 24) {
                        hourOfDay -= 12;
                    } else {
                        hourOfDay = 12;
                    }
                } else if (hourOfDay == 0) {
                    hourOfDay = 12;
                }
                if (hourOfDay < 10) {
                    hr_precede = "0";
                }
                if (minute < 10) {
                    mm_precede = "0";
                }
                hr_forTS = hr_precede + hourOfDay;
                minute_forTS = mm_precede + minute;
                taskTime = hr_precede + hourOfDay + ":" + mm_precede + minute + AM_PM;
                timeValue_TextView.setText(taskTime);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void toggleView(ConstraintLayout constraintLayout, ImageView imageView) {
        if (constraintLayout.getVisibility() == View.VISIBLE) {
            constraintLayout.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.down_arrow_icon);
        } else {
            constraintLayout.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.up_arrow_icon);
        }
    }

    @OnClick(R.id.save_Task_ImageView)
    public void saveTaskDetails() {
        boolean check = true;
        taskName = taskName_EditText.getText().toString();

        if (taskName.equals("")) {
            taskName_EditText.setError("Enter Task!");
            check = false;
        }
        if (TextUtils.isEmpty(taskDate)) {
            FancyToast.makeText(getActivity(), "Please Set Date!", Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            check = false;
        }
        if (TextUtils.isEmpty(taskTime)) {
            FancyToast.makeText(getActivity(), "Please Set Time!", Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            check = false;
        }

        if (!TextUtils.isEmpty(taskDate) && !TextUtils.isEmpty(taskTime)) {

            String date_time = taskDate;   //+ taskTime;
            Timber.d("date_time : %s", date_time);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyyy");
            Date myDate = null;
            try {
                myDate = dateFormat.parse(date_time);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
            String finalDate = timeFormat.format(myDate);
            Timber.d("finalDate : %s", finalDate);

            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(timeFormat.parse(finalDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.set(Calendar.HOUR, Integer.parseInt(hr_forTS));
            calendar.set(Calendar.MINUTE, Integer.parseInt(minute_forTS));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (AM_PM.equals(" AM")) {
                calendar.set(Calendar.AM_PM, 0);
            } else {
                calendar.set(Calendar.AM_PM, 1);
            }
            Timber.d("taskTimeStamp : " + calendar.getTimeInMillis());
            taskTimeStamp = calendar.getTimeInMillis();
        }

        taskDetailsEntity.setTask_name(taskName);
        taskDetailsEntity.setTask_priority(taskPriority);
        taskDetailsEntity.setTask_time(taskTime);
        taskDetailsEntity.setTask_date(taskDate);
        taskDetailsEntity.setTimestamp(taskTimeStamp);
        taskDetailsEntity.setTask_alarm(taskAlarm);
        taskDetailsEntity.setTask_done_status("false");
        taskDetailsEntity.setTask_catID(catId);
        taskDetailsEntity.setTask_catName(catName);
        Timber.d("taskDetailsEntity : %s", taskDetailsEntity.toString());

        if (check) {
            if (headerText.equals("Edit Task")) {
                Timber.d("Inside IF");

                taskViewModel.updateTask(taskDetailsEntity, taskId);
                FancyToast.makeText(getActivity(), taskName + " " + getString(R.string.updated), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                if (fromCalendar) {
                    goToCalendarFragment();
                } else {
                    goToTaskFragment();
                }
            } else {
                Timber.d("Inside Else");
                taskViewModel.insertTaskDetails(taskDetailsEntity);
                FancyToast.makeText(getActivity(), taskName + " " + getString(R.string.added), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                goToTaskFragment();
            }
        }
    }

    public void goToTaskFragment() {
        Task_Add_Edit_FragmentDirections.ActionTaskAddEditFragmentToTaskFragment action
                = Task_Add_Edit_FragmentDirections.actionTaskAddEditFragmentToTaskFragment();
        action.setCatId(catId);
        action.setCatName(catName);
        navController.navigate(action, navOptions);
    }

    public void goToCalendarFragment() {
        navController.navigate(R.id.action_task_Add_Edit_Fragment_to_calendarViewFragment2, null, navOptions2);
    }

    @OnClick(R.id.close_Task_ImageView)
    public void closeTaskDetails() {

        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())

                .setTitle(getString(R.string.cancel))
                .setMessage(getString(R.string.dont_want_to_add_task))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), R.drawable.save_icon, (dialogInterface, which) -> {
                    if (fromCalendar) {
                        goToCalendarFragment();
                    } else {
                        goToTaskFragment();
                    }

                    FancyToast.makeText(getActivity(), getString(R.string.canceled), FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                    dialogInterface.dismiss();
                })
                .setNegativeButton(getString(R.string.no), R.drawable.close_icon, (dialogInterface, which) -> {
                    FancyToast.makeText(getActivity(), getString(R.string.canceled), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    dialogInterface.dismiss();
                })
                .build();
        // Show Dialog
        mBottomSheetDialog.show();
    }

    public void hideSoftKeyboard(Activity activity) {

        if (editTextClick) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
        }
    }
}
