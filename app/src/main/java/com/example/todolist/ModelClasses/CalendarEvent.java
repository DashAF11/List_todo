package com.example.todolist.ModelClasses;

public class CalendarEvent {

    long taskId;
    int pTypeColor;
    long timestamp;

    @Override
    public String toString() {
        return "CalendarEvent{" +
                "taskId=" + taskId +
                ", pTypeColor=" + pTypeColor +
                ", timestamp=" + timestamp +
                '}';
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public int getpTypeColor() {
        return pTypeColor;
    }

    public void setpTypeColor(int pTypeColor) {
        this.pTypeColor = pTypeColor;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
