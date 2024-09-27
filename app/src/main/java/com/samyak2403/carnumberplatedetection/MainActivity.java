package com.samyak2403.carnumberplatedetection;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.samyak2403.carnumberplatedetection.web.GithubWebActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/samyak2403/{repo}/releases/latest";
    private static final String CHANNEL_ID = "update_notification_channel";

    private CardView liveCarNumber, addGalleryImage;
    private RelativeLayout mr_samyakkamble, license;
    private TextView versionNameTextView;

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

        // Create notification channel
        createNotificationChannel();

        // Check for app updates
        checkForUpdates(getAppVersion(), "CarNumber-Plate-Detection");
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

    private void checkForUpdates(String currentVersion, String repoName) {
        if (currentVersion == null) {
            Log.w(TAG, "Current version is null, cannot check for updates");
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String url = GITHUB_API_URL.replace("{repo}", repoName);

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch latest release", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                    String latestVersion = jsonObject.get("tag_name").getAsString();

                    if (!currentVersion.equals(latestVersion)) {
                        runOnUiThread(() -> {
                            showUpdateDialog(latestVersion, repoName);
                            sendUpdateNotification(latestVersion, repoName);
                        });
                    } else {
                        Log.i(TAG, "App is up to date. Current version: " + currentVersion);
                    }
                } else {
                    Log.e(TAG, "Error in API response: " + response.message());
                }
            }
        });
    }

    private void showUpdateDialog(String latestVersion, String repoName) {
        new AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage("A new version (" + latestVersion + ") is available. Please update to get the latest features and bug fixes.")
                .setPositiveButton("Update", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://github.com/samyak2403/" + repoName + "/releases/latest"));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Update Notifications";
            String description = "Channel for update notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendUpdateNotification(String latestVersion, String repoName) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/samyak2403/" + repoName + "/releases/latest"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_update)
                .setContentTitle("New Version Available")
                .setContentText("Version " + latestVersion + " is now available. Tap to update.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }

        notificationManager.notify(1, builder.build());
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
