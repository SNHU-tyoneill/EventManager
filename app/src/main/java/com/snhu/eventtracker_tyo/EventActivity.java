package com.snhu.eventtracker_tyo;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventActivity extends AppCompatActivity implements EventAdapter.OnDeleteClickListener {

    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM = 1;

    private EditText eventNameText, eventDateText, eventTimeText;
    private Button addEventButton;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private DatabaseHelper dbHelper;

    private Button permissionsButton;
    private ActivityResultLauncher<Intent> exactAlarmPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        eventNameText = findViewById(R.id.eventNameText);
        eventDateText = findViewById(R.id.eventDateText);
        eventTimeText = findViewById(R.id.eventTimeText);
        addEventButton = findViewById(R.id.addEventButton);
        permissionsButton = findViewById(R.id.permissionsButton);
        recyclerView = findViewById(R.id.recyclerView);

        dbHelper = new DatabaseHelper(this);
        eventList = new ArrayList<>();
        loadEvents();

        eventAdapter = new EventAdapter(this, eventList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(eventAdapter);

        exactAlarmPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (hasExactAlarmPermission()) {
                        // Re-schedule all events that require exact alarms
                        for (Event event : eventList) {
                            scheduleNotification(event);
                        }
                    } else {
                        Toast.makeText(this, "Exact Alarm Permission is required", Toast.LENGTH_SHORT).show();
                    }
                });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = eventNameText.getText().toString().trim();
                String eventDate = eventDateText.getText().toString().trim();
                String eventTime = eventTimeText.getText().toString().trim();

                if (!eventName.isEmpty() && !eventDate.isEmpty() && !eventTime.isEmpty()) {
                    Event event = new Event(0, eventName, eventDate, eventTime, false, 0);
                    saveEvent(event);
                    eventList.add(event);
                    eventAdapter.notifyItemInserted(eventList.size() - 1);
                    eventNameText.setText("");
                    eventDateText.setText("");
                    eventTimeText.setText("");

                    // Schedule notification for the new event
                    scheduleNotification(event);
                } else {
                    Toast.makeText(EventActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        permissionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PermissionsActivity.class);
            startActivity(intent);
        });

        eventNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable the add button if there is text, otherwise disable it
                addEventButton.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload events to reflect any changes in notification settings
        loadEvents();
        eventAdapter.notifyDataSetChanged();
    }

    private void loadEvents() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_EVENTS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
            int eventNameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_NAME);
            int eventDateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_DATE);
            int eventTimeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_TIME);
            int eventNotificationIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_NOTIFICATION);
            int reminderTimeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_REMINDER_TIME);

            do {
                if (idIndex >= 0 && eventNameIndex >= 0 && eventDateIndex >= 0 && eventTimeIndex >= 0 && eventNotificationIndex >= 0 && reminderTimeIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    String eventName = cursor.getString(eventNameIndex);
                    String eventDate = cursor.getString(eventDateIndex);
                    String eventTime = cursor.getString(eventTimeIndex);
                    boolean eventNotification = cursor.getInt(eventNotificationIndex) > 0;
                    int reminderTime = cursor.getInt(reminderTimeIndex);
                    eventList.add(new Event(id, eventName, eventDate, eventTime, eventNotification, reminderTime));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void saveEvent(Event event) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EVENT_NAME, event.getName());
        values.put(DatabaseHelper.COLUMN_EVENT_DATE, event.getDate());
        values.put(DatabaseHelper.COLUMN_EVENT_TIME, event.getTime());
        values.put(DatabaseHelper.COLUMN_EVENT_NOTIFICATION, event.isNotificationEnabled() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_REMINDER_TIME, event.getReminderTime());
        long id = db.insert(DatabaseHelper.TABLE_EVENTS, null, values);
        event.setId((int) id);
    }

    private void scheduleNotification(Event event) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date eventDate = sdf.parse(event.getDate() + " " + event.getTime());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(eventDate);

            // Adjust time for reminder
            if (event.isNotificationEnabled()) {
                calendar.add(Calendar.MINUTE, -event.getReminderTime());
            }

            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("event_name", event.getName());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, event.getId(), intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    requestExactAlarmPermission();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            requestExactAlarmPermission();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to schedule notification", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasExactAlarmPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestExactAlarmPermission() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        exactAlarmPermissionLauncher.launch(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        if (position >= 0 && position < eventList.size()) {
            // Delete from the database
            Event event = eventList.get(position);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(DatabaseHelper.TABLE_EVENTS, DatabaseHelper.COLUMN_EVENT_NAME + "=?", new String[]{event.getName()});

            eventList.remove(position);
            eventAdapter.notifyItemRemoved(position);
        } else {
            Toast.makeText(this, "Invalid event position", Toast.LENGTH_SHORT).show();
        }
    }
}
