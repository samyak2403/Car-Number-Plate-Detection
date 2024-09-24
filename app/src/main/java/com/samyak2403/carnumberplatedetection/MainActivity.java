package com.samyak2403.carnumberplatedetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CardView liveCarNumber, addGalleryImage, addEbook, faculty, deleteNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize all CardView elements
        liveCarNumber = findViewById(R.id.liveCarNumber);
        addGalleryImage = findViewById(R.id.addGalleryImage);
//        addEbook = findViewById(R.id.addEbook);
//        faculty = findViewById(R.id.faculty);
//        deleteNotice = findViewById(R.id.deleteNotice);

        // Set onClick listeners
        liveCarNumber.setOnClickListener(this);
        addGalleryImage.setOnClickListener(this);
//        addEbook.setOnClickListener(this);
//        faculty.setOnClickListener(this);
//        deleteNotice.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.liveCarNumber) {
            Intent intent = new Intent(MainActivity.this, LiveCarNumberDetectionActivity.class);
            startActivity(intent);
        } else if (id == R.id.addGalleryImage) {// Handle click for addGalleryImage
            Intent intent = new Intent(MainActivity.this, UploadImageNumberPlateActivity.class);
            startActivity(intent);
        }


    }
}
