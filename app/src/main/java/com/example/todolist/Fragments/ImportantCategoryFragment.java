package com.example.todolist.Fragments;

import android.app.Dialog;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.example.todolist.Adapters.ImportantCategoryAdapter;
import com.example.todolist.Dao.CategoryDao;
import com.example.todolist.Entities.CategoryEntity;
import com.example.todolist.R;
import com.example.todolist.RoomDB.RoomDB;
import com.example.todolist.ViewModel.CategoryViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.ArrayList;
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
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import timber.log.Timber;

public class ImportantCategoryFragment extends Fragment implements ImportantCategoryAdapter.ImpcategoryclickListener {
    @BindView(R.id.noImp_Constraint)
    ConstraintLayout noImp_Constraint;
    @BindView(R.id.impCategoryRecyclerView)
    RecyclerView impCategoryRecyclerView;

    CategoryDao categoryDao;
    List<CategoryEntity> categoryEntityList = new ArrayList<>();
    ImportantCategoryAdapter importantCategoryAdapter;
    CategoryViewModel categoryViewModel;
    NavController navController;

    public static ImportantCategoryFragment newInstance() {
        return new ImportantCategoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.important_category_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        categoryDao = RoomDB.getRoomDB(getActivity()).categoryDao();
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        impCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        new ItemTouchHelper(swipetoDelete).attachToRecyclerView(impCategoryRecyclerView);
        new ItemTouchHelper(swipetoEdit).attachToRecyclerView(impCategoryRecyclerView);
        impCategoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        importantCategoryAdapter = new ImportantCategoryAdapter(getActivity(), this);
        impCategoryRecyclerView.setAdapter(importantCategoryAdapter);

        getCategoryLiveData();
    }

    public void getCategoryLiveData() {
        categoryDao.getImportantCategoriesLiveData("true").observe(getViewLifecycleOwner(), (List<CategoryEntity> categoryEntities) -> {
            Timber.d("getImportantCategoriesLiveData : %s", categoryEntities.toString());

            if (categoryEntities.size() == 0) {
                noImp_Constraint.setVisibility(View.VISIBLE);
                impCategoryRecyclerView.setVisibility(View.GONE);
            } else {
                importantCategoryAdapter.setImpCategoryList(categoryEntities);
                noImp_Constraint.setVisibility(View.GONE);
                impCategoryRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    //swipe Left to delete_Cat recyclerView
    ItemTouchHelper.SimpleCallback swipetoEdit = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.dark_yellow_background))
                    .addActionIcon(R.drawable.edit_white_icon)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

            long catId = (long) viewHolder.itemView.getTag(R.id.catId);
            String catName = (String) viewHolder.itemView.getTag(R.id.catName);

            editDialogBox(catId, catName);
        }
    };

    //swipe Left to delete_Cat recyclerView
    ItemTouchHelper.SimpleCallback swipetoDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.dark_yellow_background))
                    .addActionIcon(R.drawable.delete_white_icon)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

            long catId = (long) viewHolder.itemView.getTag(R.id.catId);
            String catName = (String) viewHolder.itemView.getTag(R.id.catName);

            deleteDialogBox(catId, catName);
        }
    };

    @Override
    public void singleClickListener(View view, int position, long catId, String catName) {
        Timber.d("Category  ID : %d, Name : %s", catId, catName);

//        SharedPreferences spfUser;
//        spfUser = getActivity().getSharedPreferences(StorageConstants.KEY_CATEGORYDETAILS, Context.MODE_PRIVATE);
//        spfUser.edit().putLong(KEY_CATID, catId).apply();
//        spfUser.edit().putString(KEY_CATNAME, catName).apply();
//        navController.navigate(R.id.action_dashboardFragment_to_taskFragment);

        DashboardFragmentDirections.ActionDashboardFragmentToTaskFragment action = DashboardFragmentDirections.actionDashboardFragmentToTaskFragment();
        action.setCatId(catId);
        action.setCatName(catName);
        navController.navigate(action);
    }

    @Override
    public void categoryFavouriteListener(View view, int position, long catId, String favourite) {

    }

    public void editDialogBox(long catId, String catName) {
        final Dialog dialog = new Dialog(getActivity(),
                R.style.dialogBoxTheme);

        dialog.setContentView(R.layout.popup_edit_category_botton_layout);
        dialog.setCancelable(true);

        EditText input = (EditText) dialog.findViewById(R.id.categoryNameEdit_TextView);
        ConstraintLayout cancle = (ConstraintLayout) dialog.findViewById(R.id.cancle_constraint);
        ConstraintLayout edit = (ConstraintLayout) dialog.findViewById(R.id.edit_constraint);

        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input.setSingleLine(true);
        input.setText(catName);

        cancle.setOnClickListener(view1 -> {
            getCategoryLiveData();
            FancyToast.makeText(getActivity(), getString(R.string.canceled), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
            dialog.dismiss();
        });

        edit.setOnClickListener(v -> {
            categoryViewModel.editCategory(catId, input.getText().toString());

            FancyToast.makeText(getActivity(), getString(R.string.category_updated), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
            dialog.dismiss();
        });
        dialog.show();
    }

    public void deleteDialogBox(long catId, String catName) {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())

                .setTitle("Delete?")
                .setMessage("Are you sure want to delete this category?\nAll the task will be deleted too!")
                .setCancelable(false)
                .setPositiveButton("Delete", R.drawable.delete_white_icon, (dialogInterface, which) -> {
                    FancyToast.makeText(getActivity(), catName + " Deleted!", FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    categoryViewModel.deleteCategory(catId);

                    dialogInterface.dismiss();
                })
                .setNegativeButton("Cancel", R.drawable.close_darkpurple_icon, (dialogInterface, which) -> {
                    getCategoryLiveData();
                    FancyToast.makeText(getActivity(), getString(R.string.canceled), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    dialogInterface.dismiss();
                })
                .build();
        // Show Dialog
        mBottomSheetDialog.show();
    }
}