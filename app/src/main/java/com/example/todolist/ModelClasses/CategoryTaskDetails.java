package com.example.todolist.ModelClasses;

public class CategoryTaskDetails {

    long catId;
    int totalTask;
    int pendingTask;

    @Override
    public String toString() {
        return "CategoryTaskDetails{" +
                "catId=" + catId +
                ", totalTask=" + totalTask +
                ", pendingTask=" + pendingTask +
                '}';
    }

    public long getCatId() {
        return catId;
    }

    public void setCatId(long catId) {
        this.catId = catId;
    }

    public int getTotalTask() {
        return totalTask;
    }

    public void setTotalTask(int totalTask) {
        this.totalTask = totalTask;
    }

    public int getPendingTask() {
        return pendingTask;
    }

    public void setPendingTask(int pendingTask) {
        this.pendingTask = pendingTask;
    }
}
