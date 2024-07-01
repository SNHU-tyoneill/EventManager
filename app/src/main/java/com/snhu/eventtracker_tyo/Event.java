package com.snhu.eventtracker_tyo;

public class Event {
    private int id;
    private String name;
    private String date;
    private String time;
    private boolean notificationEnabled;
    private int reminderTime;

    public Event(int id, String name, String date, String time, boolean notificationEnabled, int reminderTime) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.notificationEnabled = notificationEnabled;
        this.reminderTime = reminderTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public int getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(int reminderTime) {
        this.reminderTime = reminderTime;
    }
}
