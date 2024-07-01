package com.snhu.eventtracker_tyo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsActivity extends AppCompatActivity {

    private static final int REQUEST_SMS_PERMISSION = 1;
    private Switch smsPermissionSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        smsPermissionSwitch = findViewById(R.id.smsPermissionSwitch);
        Button backButton = findViewById(R.id.backButton);

        // Back button click listener
        backButton.setOnClickListener(v -> {
            // Navigate back to EventActivity
            Intent intent = new Intent(PermissionsActivity.this, EventActivity.class);
            startActivity(intent);
            finish(); // Finish the current activity
        });

        // Set initial switch state
        smsPermissionSwitch.setChecked(isSmsPermissionGranted());

        // Set a listener for the switch
        smsPermissionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    requestSmsPermission();
                } else {
                    // Handle case where permission is revoked
                    Toast.makeText(PermissionsActivity.this, "SMS permission is required for notifications", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermission() {
        if (!isSmsPermissionGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                smsPermissionSwitch.setChecked(false);
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
