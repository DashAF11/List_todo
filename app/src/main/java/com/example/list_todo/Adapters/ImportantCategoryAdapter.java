package com.example.list_todo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.list_todo.Entities.CategoryEntity;
import com.example.list_todo.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ImportantCategoryAdapter extends RecyclerView.Adapter<ImportantCategoryAdapter.ViewHolder> {
    Context context;
    List<CategoryEntity> categoryEntityList = new ArrayList<>();
    ImpcategoryclickListener impcategoryclickListener;

    public ImportantCategoryAdapter(Context context, ImpcategoryclickListener impcategoryclickListener) {
        this.context = context;
        this.impcategoryclickListener = impcategoryclickListener;
    }

    public void setImpCategoryList(List<CategoryEntity> categoryEntity) {
        this.categoryEntityList = categoryEntity;
        Timber.d("categoryEntityList_Size : %s", categoryEntityList.size());
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.important_category_recyclerview_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryEntity categoryEntity = categoryEntityList.get(position);

        holder.categoryID_TextView.setText("" + categoryEntity.getCat_id());
        holder.categoryName_TextView.setText(categoryEntity.getCat_name());
        holder.importantCat_TextView.setText(categoryEntity.getFavourite());

        holder.category_constraintClick.setOnClickListener(v -> {
            long catId = categoryEntityList.get(position).getCat_id();
            String catName = categoryEntityList.get(position).getCat_name();
            impcategoryclickListener.singleClickListener(v, position, catId, catName);
            notifyItemChanged(position);
        });

        //for swipe to delete/edit value been passed to fragment
        holder.itemView.setTag(R.id.catId, categoryEntity.getCat_id());
        holder.itemView.setTag(R.id.catName, categoryEntity.getCat_name());

    }

    @Override
    public int getItemCount() {
        return categoryEntityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.category_constraintClick)
        ConstraintLayout category_constraintClick;
        @BindView(R.id.categoryID_TextView)
        TextView categoryID_TextView;
        @BindView(R.id.categoryName_TextView)
        TextView categoryName_TextView;
        @BindView(R.id.totalTask_TextView)
        TextView totalTask_TextView;
        @BindView(R.id.pendingTask_TextView)
        TextView pendingTask_TextView;
        @BindView(R.id.importantCat_TextView)
        TextView importantCat_TextView;
        @BindView(R.id.favouriteCategory_ImageView)
        ImageView favouriteCategory_ImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ImpcategoryclickListener {
        public void singleClickListener(View view, int position, long catId, String catName);

        public void categoryFavouriteListener(View view, int position, long catId, String favourite);
    }
}
