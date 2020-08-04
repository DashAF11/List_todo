package com.example.todolist.Fragments;

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

import com.example.todolist.Adapters.CategoryAdapter;
import com.example.todolist.Dao.CategoryDao;
import com.example.todolist.Dao.TaskDetailsDao;
import com.example.todolist.Entities.CategoryEntity;
import com.example.todolist.R;
import com.example.todolist.RoomDB.RoomDB;
import com.example.todolist.ViewModel.CategoryViewModel;
import com.example.todolist.ViewModel.TaskViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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
    TaskDetailsDao taskDetailsDao;
    CategoryEntity categoryEntity;
    private CategoryViewModel categoryViewModel;
    TaskViewModel taskViewModel;
    Calendar calendar;
    long timeStamp;
    List<CategoryEntity> detailedCategory = new ArrayList<>();
    List<Long> catIDs = new ArrayList<>();
    List<Long> pendingTaskList = new ArrayList<>();
    List<Long> totalTaskList = new ArrayList<>();

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
        taskDetailsDao = RoomDB.getRoomDB(getActivity()).taskDetailsDao();
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

        getCategoriesData();

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

    public void getCategoriesData() {
        categoryViewModel.getLiveCategoriesData().observe(getViewLifecycleOwner(), categoryEntities -> {
            detailedCategory.clear();
            Timber.d("getLiveCategoriesData : %s", categoryEntities.toString());
            detailedCategory.addAll(categoryEntities);

            if (categoryEntities.size() == 0) {
                nothingTODO_Constraint.setVisibility(View.VISIBLE);
                categoryRecyclerView.setVisibility(View.GONE);
                topCategory_constraint.setVisibility(View.GONE);
            } else {
                getTotalPendingDetails();
                nothingTODO_Constraint.setVisibility(View.GONE);
                categoryRecyclerView.setVisibility(View.VISIBLE);
                topCategory_constraint.setVisibility(View.VISIBLE);
            }
        });

        //mutableLiveData
//        categoryViewModel.getAllCategory();
//        categoryViewModel.getCategoryMutableLiveData().observe(getViewLifecycleOwner(), categoryEntities -> {
//        });

        //singleData
//        categoryViewModel.getCategoriesData().subscribe(new SingleObserver<List<CategoryEntity>>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//            }
//
//            @Override
//            public void onSuccess(List<CategoryEntity> categoryEntities) {
//                detailedCategory.addAll(categoryEntities);
//
//                if (detailedCategory.size() == 0) {
//                    nothingTODO_Constraint.setVisibility(View.VISIBLE);
//                    categoryRecyclerView.setVisibility(View.GONE);
//                    topCategory_constraint.setVisibility(View.GONE);
//                } else {
//                    getTotalPendingDetails();
//                    nothingTODO_Constraint.setVisibility(View.GONE);
//                    categoryRecyclerView.setVisibility(View.VISIBLE);
//                    topCategory_constraint.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Timber.e(e);
//            }
//        });
    }

    void getTotalPendingDetails() {
        catIDs.clear();
        totalTaskList.clear();
        pendingTaskList.clear();
        Completable.fromAction(() ->
        {
            catIDs = categoryViewModel.getCategoryIds();
            if (catIDs.size() != 0) {
                for (int i = 0; i < catIDs.size(); i++) {
                    totalTaskList.add(taskViewModel.totalTasks(catIDs.get(i)));
                    pendingTaskList.add(taskViewModel.pendingTasks(catIDs.get(i), "false"));
                }
                for (int i = 0; i < detailedCategory.size(); i++) {
                    detailedCategory.get(i).setTotalTask(totalTaskList.get(i));
                    detailedCategory.get(i).setPendingTask(pendingTaskList.get(i));
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        categoryAdapter.setCategoryList(detailedCategory);
                        //   Timber.d("Total_PendingTask_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
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
            editDialogBox(viewHolder.getAdapterPosition(), catId, catName);
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
            deleteDialogBox(viewHolder.getAdapterPosition(), catId, catName, "Delete Category", false);
        }
    };

    @Override
    public void singleClickListener(View view, int position, long catId, String catName) {
        Timber.d("Category  ID : %d, Name : %s", catId, catName);

        DashboardFragmentDirections.ActionDashboardFragmentToTaskFragment action = DashboardFragmentDirections.actionDashboardFragmentToTaskFragment();
        action.setCatId(catId);
        action.setCatName(catName);
        navController.navigate(action);
    }

    @Override
    public void categoryFavouriteListener(View view, int position, long catId, String favourite) {
        categoryViewModel.updateImpCategory(catId, favourite);
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

    public void deleteDialogBox(int position, long catId, String catName, String title, boolean allCategory) {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())

                .setTitle(title)
                .setMessage(getString(R.string.all_tasks_will))
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
                    categoryAdapter.notifyItemRemoved(position);
                    dialogInterface.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), R.drawable.close_darkpurple_icon, (dialogInterface, which) -> {
                    FancyToast.makeText(getActivity(), getString(R.string.canceled), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    categoryAdapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                    categoryAdapter.notifyItemRangeChanged(position, categoryAdapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                    dialogInterface.dismiss();
                })
                .build();
        // Show Dialog
        mBottomSheetDialog.show();
    }

    public void editDialogBox(int position, long catId, String catName) {
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
            categoryAdapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
            categoryAdapter.notifyItemRangeChanged(position, categoryAdapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
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
        deleteDialogBox(0, 0, null, "Delete All Categories?", true);
    }

    @OnClick(R.id.back_ImageView)
    void backSearchViewClick() {
        toggleView(searchHider_Constraint, searchCategory_TextView, deleteAllCategories_TextView);
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