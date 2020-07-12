package com.example.todolist.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import com.example.todolist.Entities.CategoryEntity;
import com.example.todolist.Entities.TaskDetailsEntity;
import com.example.todolist.R;
import com.example.todolist.ViewModel.CategoryViewModel;
import com.example.todolist.ViewModel.TaskViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class QuickTaskAddFragment extends Fragment {

    @BindView(R.id.headerTask_TextView)
    TextView headerTask_TextView;
    @BindView(R.id.dateValue_TextView)
    TextView dateValue_TextView;
    @BindView(R.id.timeValue_TextView)
    TextView timeValue_TextView;
    @BindView(R.id.priorityValue_TextView)
    TextView priorityValue_TextView;

    @BindView(R.id.dattimeHider_Constraint)
    ConstraintLayout dattimeHider_Constraint;
    @BindView(R.id.priorityHider_Constraint)
    ConstraintLayout priorityHider_Constraint;

    @BindView(R.id.priorityArrow_ImageView)
    ImageView priorityArrow_ImageView;

    @BindView(R.id.taskName_EditText)
    EditText taskName_EditText;

    @BindView(R.id.alarm_Switch)
    SwitchCompat alarm_Switch;
    @BindView(R.id.setDateTime_Switch)
    SwitchCompat setDateTime_Switch;

    @BindView(R.id.category_Spinner)
    Spinner category_Spinner;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.high_RadioButton)
    RadioButton high_RadioButton;
    @BindView(R.id.med_RadioButton)
    RadioButton med_RadioButton;
    @BindView(R.id.low_RadioButton)
    RadioButton low_RadioButton;

    TaskViewModel taskViewModel;
    CategoryViewModel categoryViewModel;
    CategoryEntity categoryEntity = new CategoryEntity();
    TaskDetailsEntity taskDetailsEntity = new TaskDetailsEntity();
    NavController navController;
    NavOptions navOptions;

    ArrayList<String> categoryList = new ArrayList<>();
    String headerText, catName, taskName, taskDate, taskTime, taskPriority = "low", taskAlarm = "false",
            mytimeHR_M, hr_forTS, minute_forTS, AM_PM, spinnerCatName;
    boolean taskSet_DT = false;
    long taskTimeStamp, catId, taskId;
    int mYear, mMonth, mDay, mHour, mMinute, day_forTS, month_forTS, year_forTS;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.quick_add_task_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            QuickTaskAddFragmentArgs args = QuickTaskAddFragmentArgs.fromBundle(getArguments());
            headerText = args.getHeaderName();
            headerTask_TextView.setText(headerText);
        }

        navController = Navigation.findNavController(view);
        navOptions = new NavOptions.Builder().setPopUpTo(R.id.dashboardFragment, true).build();

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        taskName_EditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        taskName_EditText.setSingleLine(true);

        low_RadioButton.setChecked(true);

        categoryViewModel.getAllCategoriesNamesLiveData().observe(getViewLifecycleOwner(), strings -> {
            categoryList.clear();

            categoryList.add(0, "None");
            categoryList.add(1, "ADD NEW CATEGORY");
            categoryList.addAll(strings);
            //  Timber.d("categoryList %s", categoryList);

            ArrayAdapter<String> catname_Adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item, categoryList);

            catname_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            category_Spinner.setAdapter(catname_Adapter);
            category_Spinner.setSelection(0);

            category_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    hideKeyboard(requireActivity());

                    if (position == 1) {
                        addCategory();
                    } else {
                        catName = parent.getItemAtPosition(position).toString();
                        //    Timber.d("catName : %s", catName);
                        getCatIdBy_Name(catName);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
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

        setDateTime_Switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                dattimeHider_Constraint.setVisibility(View.VISIBLE);
                taskSet_DT = true;
            } else {
                dattimeHider_Constraint.setVisibility(View.GONE);
                taskSet_DT = false;
            }
        });
    }

    @OnClick(R.id.priority_Constraint)
    public void priority_ArrowClick() {
        toggleView(priorityHider_Constraint, priorityArrow_ImageView);
    }

    @OnClick(R.id.date_Constraint)
    public void dateClick() {

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

        if (TextUtils.isEmpty(taskName)) {
            taskName_EditText.setError("Enter Task!");
            check = false;
        }

        if (taskSet_DT) {
            if (TextUtils.isEmpty(taskDate)) {
                FancyToast.makeText(getActivity(), "Please Set Date!", Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                check = false;
            }
            if (TextUtils.isEmpty(taskTime)) {
                FancyToast.makeText(getActivity(), "Please Set Time!", Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                check = false;
            }
        }

        if (!TextUtils.isEmpty(taskDate) && !TextUtils.isEmpty(taskTime)) {
            String date_time = taskDate;   //+ taskTime;
            // Timber.d("date_time : %s", date_time);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyyy");
            Date myDate = null;
            try {
                myDate = dateFormat.parse(date_time);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
            String finalDate = timeFormat.format(myDate);
            // Timber.d("finalDate :" + finalDate);

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
            //  Timber.d("taskTimeStamp : %d", calendar.getTimeInMillis());
            taskTimeStamp = calendar.getTimeInMillis();
        }

        if (check) {
            taskDetailsEntity.setTask_name(taskName);
            if (taskSet_DT) {
                taskDetailsEntity.setTask_time(taskTime);
                taskDetailsEntity.setTask_date(taskDate);
                taskDetailsEntity.setTimestamp(taskTimeStamp);
            } else {
                taskDetailsEntity.setTask_time("false");
                taskDetailsEntity.setTask_date("false");
                taskDetailsEntity.setTimestamp(0);
            }
            taskDetailsEntity.setTask_done_status("false");
            taskDetailsEntity.setTask_priority(taskPriority);
            taskDetailsEntity.setTask_alarm(taskAlarm);
            taskDetailsEntity.setTask_catName(catName);
            taskDetailsEntity.setTask_catID(catId);

            Timber.d("taskDetailsEntity : %s", taskDetailsEntity.toString());
            taskViewModel.insertTaskDetails(taskDetailsEntity);
            FancyToast.makeText(getActivity(), taskName + " " + getString(R.string.created), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
            goToDashBoard();
        }
    }

    private void goToDashBoard() {
        navController.navigate(R.id.action_quickTaskAddFragment_to_dashboardFragment, null, navOptions);
    }

    @OnClick(R.id.close_Task_ImageView)
    public void closeTaskDetails() {

        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())

                .setTitle(getString(R.string.cancel))
                .setMessage(getString(R.string.dont_want_to_add_task))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), R.drawable.save_icon, (dialogInterface, which) -> {
                    goToDashBoard();

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

    public void addCategory() {
        Calendar calendar = Calendar.getInstance();
        long timeStamp = calendar.getTimeInMillis();

        final Dialog dialog = new Dialog(getActivity(),
                R.style.dialogBoxTheme);

        dialog.setContentView(R.layout.popup_add_category_botton_layout);
        dialog.setCancelable(true);

        EditText input = (EditText) dialog.findViewById(R.id.categoryNameAdd_TextView);
        ConstraintLayout cancle = (ConstraintLayout) dialog.findViewById(R.id.cancle_constraint);
        ConstraintLayout addCategory = (ConstraintLayout) dialog.findViewById(R.id.add_constraint);

        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input.setSingleLine(true);

        cancle.setOnClickListener(view1 -> {
            category_Spinner.setSelection(0);
            FancyToast.makeText(getActivity(), getString(R.string.canceled), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
            dialog.dismiss();
        });

        addCategory.setOnClickListener(v -> {

            if (input.getText().toString().length() == 0) {
                input.setError("Enter Category!");
                return;
            }
            categoryEntity.setCat_name(input.getText().toString());
            categoryEntity.setTimestamp(timeStamp);
            categoryEntity.setFavourite("false");

            categoryViewModel.addCategory(categoryEntity);
            category_Spinner.setSelection(3);
            FancyToast.makeText(getActivity(), input.getText().toString() + " Created!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
            dialog.dismiss();

        });
        dialog.show();
    }

    void getCatIdBy_Name(String catName) {
        Completable.fromAction(() -> {
            catId = categoryViewModel.getIDbyCategoryName(catName);
            //  Timber.d("catId : %d", catId);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("getCatIdBy_Name_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    private void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().getDecorView();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }
}