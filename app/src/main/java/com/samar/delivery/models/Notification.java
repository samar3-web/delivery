package com.samar.delivery.models;

import java.util.Date;

// Notification.java
public class Notification {
    private String taskTitle;
    private Date notificationDate;

    public Notification(String taskTitle, Date notificationDate) {
        this.taskTitle = taskTitle;
        this.notificationDate = notificationDate;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }
}

