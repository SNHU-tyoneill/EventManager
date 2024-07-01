package com.snhu.eventtracker_tyo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserAuth {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public UserAuth(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public boolean registerUser(String username, String password) {
        if (isUserExists(username)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, username);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);
        long result = database.insert(DatabaseHelper.TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean loginUser(String username, String password) {
        String[] columns = {
                DatabaseHelper.COLUMN_ID
        };
        String selection = DatabaseHelper.COLUMN_USERNAME + " = ?" + " AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        int cursorCount = cursor.getCount();
        cursor.close();

        return cursorCount > 0;
    }

    private boolean isUserExists(String username) {
        String[] columns = {
                DatabaseHelper.COLUMN_ID
        };
        String selection = DatabaseHelper.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        int cursorCount = cursor.getCount();
        cursor.close();

        return cursorCount > 0;
    }
}
