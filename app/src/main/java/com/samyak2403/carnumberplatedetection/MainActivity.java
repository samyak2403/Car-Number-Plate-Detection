package com.samyak2403.carnumberplatedetection;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.samyak2403.carnumberplatedetection.web.GithubWebActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CardView liveCarNumber, addGalleryImage;
    RelativeLayout mr_samyakkamble, license;
    TextView versionNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize all CardView, RelativeLayout, and TextView elements
        liveCarNumber = findViewById(R.id.liveCarNumber);
        addGalleryImage = findViewById(R.id.addGalleryImage);
        mr_samyakkamble = findViewById(R.id.mr_samyakkamble);
        license = findViewById(R.id.license);
        versionNameTextView = findViewById(R.id.versionName); // TextView for version name

        // Set onClick listeners
        liveCarNumber.setOnClickListener(this);
        addGalleryImage.setOnClickListener(this);
        mr_samyakkamble.setOnClickListener(this);
        license.setOnClickListener(this);

        // Get app version and set it to the TextView
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            versionNameTextView.setText("Version: " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionNameTextView.setText("Version: N/A");
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
