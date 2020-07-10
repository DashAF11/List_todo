package com.example.todolist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Entities.CategoryEntity;
import com.example.todolist.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> implements Filterable {

    Context context;
    List<CategoryEntity> categoryEntityList = new ArrayList<>();
    List<CategoryEntity> categorySearchList = new ArrayList<>();
    CategoryClickListener categoryClickListener;

    public CategoryAdapter(Context context, CategoryClickListener categoryClickListener) {
        this.context = context;
        this.categoryClickListener = categoryClickListener;
    }

    public void setCategoryList(List<CategoryEntity> categoryEntities) {
        this.categoryEntityList = categoryEntities;
        this.categorySearchList.addAll(categoryEntities);
//        Timber.d("categoryEntities_Size : %s", categoryEntityList.size());
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_recyclerview_layout, viewGroup, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {

        CategoryEntity categoryEntity = categoryEntityList.get(position);

        holder.categoryID_TextView.setText("" + categoryEntity.getCat_id());
        holder.categoryName_TextView.setText(categoryEntity.getCat_name());
        holder.importantCat_TextView.setText(categoryEntity.getFavourite());

        if (categoryEntityList.get(position).getFavourite().equals("false")) {
            holder.favouriteCategory_ImageView.setBackgroundResource(R.drawable.important_icon_white);
        } else {
            holder.favouriteCategory_ImageView.setBackgroundResource(R.drawable.important_icon);
        }

        holder.favouriteCategory_ImageView.setOnClickListener(v -> {
            Timber.d("Favourite_TextView : %s", holder.importantCat_TextView.getText().toString());
            Timber.d("Favourite_categoryEntityList : %s", categoryEntityList.get(position).getFavourite());

            if (holder.importantCat_TextView.getText().toString().equals("false")) {
                categoryClickListener.categoryFavouriteListener(v, position, categoryEntity.getCat_id(), "true");
                categoryEntityList.get(position).setFavourite("true");

                // holder.favouriteCategory_ImageView.setImageResource(R.drawable.fav_red_icon);
                notifyItemChanged(position);
            } else if (categoryEntityList.get(position).getFavourite().equals("true")) {
                categoryClickListener.categoryFavouriteListener(v, position, categoryEntity.getCat_id(), "false");
                categoryEntityList.get(position).setFavourite("false");

                //holder.favouriteCategory_ImageView.setImageResource(R.drawable.fav_white_icon);
                notifyItemChanged(position);
            }
        });

        holder.category_constraintClick.setOnClickListener((View v) -> {
            long catId = categoryEntityList.get(position).getCat_id();
            String catName = categoryEntityList.get(position).getCat_name();
            categoryClickListener.singleClickListener(v, position, catId, catName);
            notifyItemChanged(position);
        });

        holder.itemView.setTag(R.id.catId, categoryEntity.getCat_id());
        holder.itemView.setTag(R.id.catName, categoryEntity.getCat_name());
    }

    @Override
    public int getItemCount() {
        return categoryEntityList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {

        //runs on background Thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<CategoryEntity> filterList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filterList.addAll(categorySearchList);
            } else {
                for (CategoryEntity categoryEntity : categorySearchList) {
                    if (categoryEntity.getCat_name().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filterList.add(categoryEntity);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        //runs on UI Thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            categoryEntityList.clear();
            categoryEntityList.addAll((Collection<? extends CategoryEntity>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class CategoryHolder extends RecyclerView.ViewHolder {
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

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface CategoryClickListener {
        public void singleClickListener(View view, int position, long catId, String catName);

        public void categoryFavouriteListener(View view, int position, long catId, String favourite);
    }
}
