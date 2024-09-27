package com.samyak2403.carnumberplatedetection;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.samyak2403.carnumberplatedetection.web.GithubWebActivity;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private CardView liveCarNumber, addGalleryImage;
    private RelativeLayout mr_samyakkamble, license;
    private TextView versionNameTextView;
    private ImageButton toolbar_update_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        initViews();

        // Set onClick listeners
        setListeners();

        // Get app version and display
        displayAppVersion();


    }

    private void initViews() {
        liveCarNumber = findViewById(R.id.liveCarNumber);
        addGalleryImage = findViewById(R.id.addGalleryImage);
        mr_samyakkamble = findViewById(R.id.mr_samyakkamble);
        license = findViewById(R.id.license);
        versionNameTextView = findViewById(R.id.versionName);

    }

    private void setListeners() {
        liveCarNumber.setOnClickListener(this);
        addGalleryImage.setOnClickListener(this);
        mr_samyakkamble.setOnClickListener(this);
        license.setOnClickListener(this);
        toolbar_update_button.setOnClickListener(this);
    }

    private void displayAppVersion() {
        String version = getAppVersion();
        versionNameTextView.setText("Version: " + (version != null ? version : "N/A"));
    }

    private String getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get app version", e);
            return null;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.liveCarNumber) {
            Intent intent = new Intent(MainActivity.this, LiveCarNumberDetectionActivity.class);
            startActivity(intent);
        } else if (id == R.id.addGalleryImage) {
            Intent intent = new Intent(MainActivity.this, UploadImageNumberPlateActivity.class);
            startActivity(intent);
        } else if (id == R.id.mr_samyakkamble) {
            Intent intent = new Intent(MainActivity.this, GithubWebActivity.class);
            startActivity(intent);
        } else if (id == R.id.license) {
            Intent intent = new Intent(MainActivity.this, LicenseActivity.class);
            startActivity(intent);
        }
    }


}
