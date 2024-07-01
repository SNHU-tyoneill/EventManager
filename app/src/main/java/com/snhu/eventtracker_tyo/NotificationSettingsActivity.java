package com.snhu.eventtracker_tyo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationSettingsActivity extends AppCompatActivity {

    private Switch reminderSwitch;
    private TimePicker timePicker;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        reminderSwitch = findViewById(R.id.reminderSwitch);
        timePicker = findViewById(R.id.timePicker);
        saveButton = findViewById(R.id.saveButton);
        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        eventName = intent.getStringExtra("event_name");

        saveButton.setOnClickListener(v -> {
            boolean isReminderEnabled = reminderSwitch.isChecked();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            int reminderTime = hour * 60 + minute;

            saveNotificationSettings(eventName, isReminderEnabled, reminderTime);

            finish();
        });

        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> timePicker.setEnabled(isChecked));
    }

    private void saveNotificationSettings(String eventName, boolean isReminderEnabled, int reminderTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EVENT_NOTIFICATION, isReminderEnabled ? 1 : 0);
        db.update(DatabaseHelper.TABLE_EVENTS, values, DatabaseHelper.COLUMN_EVENT_NAME + "=?", new String[]{eventName});
    }
}
