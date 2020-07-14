package com.example.todolist.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.todolist.Entities.CategoryEntity;
import com.example.todolist.R;
import com.example.todolist.ViewModel.CategoryViewModel;
import com.example.todolist.ViewModel.TaskViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.todolist.Constants.Keys.KEY_NavPage;
import static com.example.todolist.Constants.StorageConstants.TODO_USER;

public class DashboardFragment extends Fragment {

    @BindView(R.id.topName_TextView)
    TextView topName_TextView;
    @BindView(R.id.group_TextView)
    TextView group_TextView;
    @BindView(R.id.delayed_TextView)
    TextView delayed_TextView;
    @BindView(R.id.done_TextView)
    TextView done_TextView;
    @BindView(R.id.imp_TextView)
    TextView imp_TextView;
    @BindView(R.id.categoryCount_TextView)
    TextView categoryCount_TextView;
    @BindView(R.id.delayCount_TextView)
    TextView delayCount_TextView;
    @BindView(R.id.doneCount_TextView)
    TextView doneCount_TextView;
    @BindView(R.id.impCategoryCount_TextView)
    TextView impCategoryCount_TextView;

    @BindView(R.id.categoryCount_Constraint)
    ConstraintLayout categoryCount_Constraint;
    @BindView(R.id.delayCount_Constraint)
    ConstraintLayout delayCount_Constraint;
    @BindView(R.id.doneCount_Constraint)
    ConstraintLayout doneCount_Constraint;
    @BindView(R.id.impCategoryCount_Constraint)
    ConstraintLayout impCategoryCount_Constraint;

    @BindView(R.id.dashBoard_container_frameLayout)
    FrameLayout dashBoard_container_frameLayout;

    FragmentManager fragmentManager;
    CategoryEntity categoryEntity;
    private CategoryViewModel categoryViewModel;
    TaskViewModel taskViewModel;
    Calendar calendar;
    long timeStamp;
    NavController navController;
    SharedPreferences spfUser;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void replaceFragment(@IdRes int containerViewId,
                                @NonNull Fragment fragment,
                                @NonNull String fragmentTag,
                                @Nullable String backStackStateName) {
        fragmentManager.beginTransaction()
                //   .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(containerViewId, fragment, fragmentTag)
                .commitAllowingStateLoss();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentManager = getActivity().getSupportFragmentManager();
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        navController = Navigation.findNavController(view);
        calendar = Calendar.getInstance();

        spfUser = requireActivity().getSharedPreferences(TODO_USER, Context.MODE_PRIVATE);

        String navPage = spfUser.getString(KEY_NavPage, "Category");
//        Timber.d("NavigationPage ; %s", navPage);

        switch (navPage) {
            case "Category":
                toggleGroup();
                break;
            case "Delayed Tasks":
                toggleDelayed();
                break;
            case "Done Tasks":
                toggleDone();
                break;
            case "Important Category":
                toggleImp();
                break;
        }
        getCategoryCount();
        getDelayedCount();
        getDoneCount();
        getImpCategoryCount();
    }

    private void toggleView(TextView textView) {
        if (textView.getVisibility() == View.VISIBLE) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.group_ImageView)
    public void toggleGroup() {
        spfUser.edit().putString(KEY_NavPage, "Category").apply();
        getTopName("Category");
        toggleView(group_TextView);
        delayed_TextView.setVisibility(View.GONE);
        done_TextView.setVisibility(View.GONE);
        imp_TextView.setVisibility(View.GONE);
        groupClick();
    }

    @OnClick(R.id.delayed_ImageView)
    public void toggleDelayed() {
        spfUser.edit().putString(KEY_NavPage, "Delayed Tasks").apply();
        getTopName("Delayed Tasks");
        toggleView(delayed_TextView);
        group_TextView.setVisibility(View.GONE);
        done_TextView.setVisibility(View.GONE);
        imp_TextView.setVisibility(View.GONE);
        delayedClick();
    }

    @OnClick(R.id.done_ImageView)
    public void toggleDone() {
        spfUser.edit().putString(KEY_NavPage, "Done Tasks").apply();
        getTopName("Done Tasks");
        toggleView(done_TextView);
        group_TextView.setVisibility(View.GONE);
        delayed_TextView.setVisibility(View.GONE);
        imp_TextView.setVisibility(View.GONE);
        doneClick();
    }

    @OnClick(R.id.imp_ImageView)
    public void toggleImp() {
        spfUser.edit().putString(KEY_NavPage, "Important Category").apply();
        getTopName("Important Category");
        toggleView(imp_TextView);
        group_TextView.setVisibility(View.GONE);
        delayed_TextView.setVisibility(View.GONE);
        done_TextView.setVisibility(View.GONE);
        impClick();
    }

    public void groupClick() {
        replaceFragment(R.id.dashBoard_container_frameLayout, CategoryFragment.newInstance(), CategoryFragment.class.getSimpleName(), null);
    }

    public void delayedClick() {
        replaceFragment(R.id.dashBoard_container_frameLayout, DelayedTaskFragment.newInstance(), DelayedTaskFragment.class.getSimpleName(), null);
    }

    public void doneClick() {
        replaceFragment(R.id.dashBoard_container_frameLayout, DoneTaskFragment.newInstance(), DoneTaskFragment.class.getSimpleName(), null);
    }

    public void impClick() {
        replaceFragment(R.id.dashBoard_container_frameLayout, ImportantCategoryFragment.newInstance(), ImportantCategoryFragment.class.getSimpleName(), null);
    }

    public void addCategory() {
        categoryEntity = new CategoryEntity();
        timeStamp = calendar.getTimeInMillis();

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

            FancyToast.makeText(getActivity(), input.getText().toString() + " Added!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
            dialog.dismiss();

        });
        dialog.show();
    }

    @OnClick(R.id.addBoth_ImageView)
    public void addQuickDialogBox() {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())

                .setTitle(getString(R.string.want_to_add_something))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.task), R.drawable.add_icon, (dialogInterface, which) -> {
                    addTask("Add Task");
                    dialogInterface.dismiss();
                })
                .setNegativeButton(getString(R.string.category), R.drawable.add_icon_purple, (dialogInterface, which) -> {
                    addCategory();
                    dialogInterface.dismiss();
                })
                .build();
        // Show Dialog
        mBottomSheetDialog.show();
    }

    void addTask(String header) {
        DashboardFragmentDirections.ActionDashboardFragmentToQuickTaskAddFragment action = DashboardFragmentDirections.actionDashboardFragmentToQuickTaskAddFragment();
        action.setHeaderName("Add Task");
        navController.navigate(action);
    }

    public void getCategoryCount() {
        categoryViewModel.totalCategoryCount().observe(getViewLifecycleOwner(), count -> {
            //  Timber.d("totalCategoryCount : " + count);
            categoryCount_TextView.setText("" + count);
        });
    }

    public void getDelayedCount() {
        Calendar calendar = Calendar.getInstance();
        long currentTimeStamp = calendar.getTimeInMillis();
        // Timber.d("currentTimeStamp : " + currentTimeStamp);

        taskViewModel.totalDelayTaskCount(currentTimeStamp, "false").observe(getViewLifecycleOwner(), count -> {
            //    Timber.d("totalDelayTaskCount : " + count);
            delayCount_TextView.setText("" + count);
        });
    }

    public void getDoneCount() {
        taskViewModel.totalDoneTaskCount().observe(getViewLifecycleOwner(), count -> {
            //  Timber.d("totalDoneTaskCount : " + count);
            doneCount_TextView.setText("" + count);
        });
    }

    public void getImpCategoryCount() {
        categoryViewModel.totalImpCategoryCount().observe(getViewLifecycleOwner(), count -> {
            //  Timber.d("totalImpCategoryCount : " + count);
            impCategoryCount_TextView.setText("" + count);
        });
    }

    void getTopName(String name) {
        topName_TextView.setText(name);
    }

    @OnClick(R.id.calendar_ImageView)
    void calendarClick() {
        navController.navigate(R.id.action_dashboardFragment_to_calendarViewFragment);
    }

    @OnClick(R.id.version_ImageView)
    void getVersionName() {
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;

            FancyToast.makeText(getActivity(), "Version : " + version, Toast.LENGTH_SHORT, FancyToast.INFO, false).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}