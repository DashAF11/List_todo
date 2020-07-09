package com.example.list_todo.Fragments;

import android.app.Dialog;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.list_todo.Adapters.CategoryAdapter;
import com.example.list_todo.Dao.CategoryDao;
import com.example.list_todo.Entities.CategoryEntity;
import com.example.list_todo.R;
import com.example.list_todo.RoomDB.RoomDB;
import com.example.list_todo.ViewModel.CategoryViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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

public class CategoryFragment extends Fragment implements CategoryAdapter.CategoryClickListener {

    @BindView(R.id.searchCategory_TextView)
    TextView searchCategory_TextView;
    @BindView(R.id.deleteAllCategories_TextView)
    TextView deleteAllCategories_TextView;

    @BindView(R.id.back_ImageView)
    ImageView back_ImageView;

    @BindView(R.id.catgory_SearchView)
    SearchView catgory_SearchView;

    @BindView(R.id.topCategory_constraint)
    ConstraintLayout topCategory_constraint;
    @BindView(R.id.searchHider_Constraint)
    ConstraintLayout searchHider_Constraint;
    @BindView(R.id.nothingTODO_Constraint)
    ConstraintLayout nothingTODO_Constraint;
    @BindView(R.id.categoryRecyclerView)
    RecyclerView categoryRecyclerView;

    NavController navController;
    CategoryAdapter categoryAdapter;
    CategoryDao categoryDao;
    CategoryEntity categoryEntity;
    private CategoryViewModel categoryViewModel;
    TaskViewModel taskViewModel;
    Calendar calendar;
    long timeStamp;

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        categoryDao = RoomDB.getRoomDB(getActivity()).categoryDao();
        calendar = Calendar.getInstance();
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        categoryEntity = new CategoryEntity();
        timeStamp = calendar.getTimeInMillis();

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        new ItemTouchHelper(swipetoDelete).attachToRecyclerView(categoryRecyclerView);
        new ItemTouchHelper(swipetoEdit).attachToRecyclerView(categoryRecyclerView);
        categoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        categoryAdapter = new CategoryAdapter(getActivity(), this);
        categoryRecyclerView.setAdapter(categoryAdapter);

        getCategoriesLivData();

        catgory_SearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Timber.d("catgory_SearchView : %s", newText);
                categoryAdapter.getFilter().filter(newText);
                categoryAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    public void getCategoriesLivData() {

        categoryViewModel.getLiveCategoriesData().observe(getViewLifecycleOwner(), categoryEntities -> {

            Timber.d("getAllCategoriesLiveData : %s", categoryEntities.toString());
            if (categoryEntities.size() == 0) {
                nothingTODO_Constraint.setVisibility(View.VISIBLE);
                categoryRecyclerView.setVisibility(View.GONE);
                topCategory_constraint.setVisibility(View.GONE);
            } else {
                categoryAdapter.setCategoryList(categoryEntities);
                nothingTODO_Constraint.setVisibility(View.GONE);
                categoryRecyclerView.setVisibility(View.VISIBLE);
                topCategory_constraint.setVisibility(View.VISIBLE);
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
                    .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.dark_purple_background))
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
                    .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.dark_purple_background))
                    .addActionIcon(R.drawable.delete_white_icon)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

            long catId = (long) viewHolder.itemView.getTag(R.id.catId);
            String catName = (String) viewHolder.itemView.getTag(R.id.catName);

            deleteDialogBox(catId, catName, "Delete Category", "All the tasks in this categories will be deleted too!", false);
        }
    };

    @Override
    public void singleClickListener(View view, int position, long catId, String catName) {
        Timber.d("Category  ID : %d, Name : %s", catId, catName);

        //to pass data
        DashboardFragmentDirections.ActionDashboardFragmentToTaskFragment action = DashboardFragmentDirections.actionDashboardFragmentToTaskFragment();
        action.setCatId(catId);
        action.setCatName(catName);
        navController.navigate(action);
    }

    @Override
    public void categoryFavouriteListener(View view, int position, long catId, String favourite) {
        categoryViewModel.updateFavouriteCategory(catId, favourite);
    }

    @OnClick(R.id.addCategory_ImageView)
    public void addCategory() {

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
            categoryEntity.setCat_name(input.getText().toString());
            categoryEntity.setTimestamp(timeStamp);
            categoryEntity.setFavourite("false");

            categoryViewModel.addCategory(categoryEntity);

            FancyToast.makeText(getActivity(), input.getText().toString() + " Added!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();

            dialog.dismiss();
        });
        dialog.show();
    }

    public void deleteDialogBox(long catId, String catName, String title, String message, boolean allCategory) {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())

                .setTitle(title)
                .setMessage("All the task will be deleted too!")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.delete), R.drawable.delete_white_icon, (dialogInterface, which) -> {

                    if (allCategory) {
                        FancyToast.makeText(getActivity(), getString(R.string.all_categories_deleted), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                        categoryViewModel.deleteAllCategory();
                        taskViewModel.deleteAllTasks();
                    } else {
                        FancyToast.makeText(getActivity(), catName + " " + getString(R.string.deleted), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                        categoryViewModel.deleteCategory(catId);
                        taskViewModel.deleteAllTask_byCatID(catId);
                    }

                    dialogInterface.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), R.drawable.close_darkpurple_icon, (dialogInterface, which) -> {
                    FancyToast.makeText(getActivity(), getString(R.string.canceled), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    getCategoriesLivData();
                    dialogInterface.dismiss();
                })
                .build();
        // Show Dialog
        mBottomSheetDialog.show();
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
        input.setSelection(input.getText().length());

        cancle.setOnClickListener(view1 -> {
            getCategoriesLivData();
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

    @OnClick(R.id.searchCategory_TextView)
    void searchTextClick() {
        toggleView(searchHider_Constraint, searchCategory_TextView, deleteAllCategories_TextView);
    }

    @OnClick(R.id.deleteAllCategories_TextView)
    void deleteAllCategories() {
        deleteDialogBox(0, null, "Delete All Categories?", "All the tasks in this categories will be deleted too!", true);
    }

    @OnClick(R.id.back_ImageView)
    void backClick() {
        toggleView(searchHider_Constraint, searchCategory_TextView, deleteAllCategories_TextView);
        getCategoriesLivData();
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
}