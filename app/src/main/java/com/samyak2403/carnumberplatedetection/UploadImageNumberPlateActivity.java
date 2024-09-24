package com.samyak2403.carnumberplatedetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadImageNumberPlateActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView numberPlateImageView;
    private TextView numberPlateTextView;
    private TextRecognizer textRecognizer;
    private Uri imageUri;

    // Map for Indian states and UTs vehicle registration codes
    private static final Map<String, String> STATE_MAP = new HashMap<>();
    static {
        STATE_MAP.put("AP", "Andhra Pradesh");
        STATE_MAP.put("AR", "Arunachal Pradesh");
        STATE_MAP.put("AS", "Assam");
        STATE_MAP.put("BR", "Bihar");
        STATE_MAP.put("CG", "Chhattisgarh");
        STATE_MAP.put("GA", "Goa");
        STATE_MAP.put("GJ", "Gujarat");
        STATE_MAP.put("HR", "Haryana");
        STATE_MAP.put("HP", "Himachal Pradesh");
        STATE_MAP.put("JH", "Jharkhand");
        STATE_MAP.put("KA", "Karnataka");
        STATE_MAP.put("KL", "Kerala");
        STATE_MAP.put("MP", "Madhya Pradesh");
        STATE_MAP.put("MH", "Maharashtra");
        STATE_MAP.put("MN", "Manipur");
        STATE_MAP.put("ML", "Meghalaya");
        STATE_MAP.put("MZ", "Mizoram");
        STATE_MAP.put("NL", "Nagaland");
        STATE_MAP.put("OD", "Odisha");
        STATE_MAP.put("PB", "Punjab");
        STATE_MAP.put("RJ", "Rajasthan");
        STATE_MAP.put("SK", "Sikkim");
        STATE_MAP.put("TN", "Tamil Nadu");
        STATE_MAP.put("TG", "Telangana");
        STATE_MAP.put("TR", "Tripura");
        STATE_MAP.put("UP", "Uttar Pradesh");
        STATE_MAP.put("UK", "Uttarakhand");
        STATE_MAP.put("WB", "West Bengal");
        STATE_MAP.put("AN", "Andaman and Nicobar Islands");
        STATE_MAP.put("CH", "Chandigarh");
        STATE_MAP.put("DN", "Dadra and Nagar Haveli and Daman and Diu");
        STATE_MAP.put("DL", "Delhi");
        STATE_MAP.put("JK", "Jammu and Kashmir");
        STATE_MAP.put("LA", "Ladakh");
        STATE_MAP.put("LD", "Lakshadweep");
        STATE_MAP.put("PY", "Puducherry");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image_number_plate);

        MaterialCardView addGalleryImage = findViewById(R.id.addGalleryImage);
        Button uploadImageBtn = findViewById(R.id.uploadImageBtn);
        numberPlateImageView = findViewById(R.id.NumberPlateImageView);
        numberPlateTextView = findViewById(R.id.numberPlateTextView);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        addGalleryImage.setOnClickListener(v -> openImageChooser());

        uploadImageBtn.setOnClickListener(v -> {
            if (imageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    numberPlateImageView.setImageBitmap(bitmap);
                    processImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }

    private void processImage(Bitmap bitmap) {
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        textRecognizer.process(inputImage)
                .addOnSuccessListener(visionText -> {
                    String detectedText = extractNumberPlateText(visionText);
                    if (!detectedText.isEmpty()) {
                        numberPlateTextView.setText(detectedText);
                    } else {
                        Toast.makeText(this, "No number plate detected", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("MLKit", "Text recognition failed: " + e.getMessage()));
    }

    private String extractNumberPlateText(Text visionText) {
        StringBuilder numberPlateText = new StringBuilder();
        String regexPattern = "([A-Z]{2}[0-9]{1,2}[A-Z]{1,2}[0-9]{4})";
        Pattern pattern = Pattern.compile(regexPattern);

        for (Text.TextBlock block : visionText.getTextBlocks()) {
            String blockText = block.getText();
            Matcher matcher = pattern.matcher(blockText);
            while (matcher.find()) {
                String numberPlate = matcher.group();
                numberPlateText.append(formatNumberPlateDetails(numberPlate)).append("\n\n");
            }
        }
        return numberPlateText.toString();
    }

    private String formatNumberPlateDetails(String numberPlate) {
        String stateCode = numberPlate.substring(0, 2);
        String state = STATE_MAP.getOrDefault(stateCode, "Unknown State");
        String vehicleType = identifyVehicleType(numberPlate);
        String numberPlateColor = identifyNumberPlateColor(vehicleType);

        return "Number Plate: " + numberPlate +
                "\nState: " + state +
                "\nVehicle Type: " + vehicleType +
                "\nNumber Plate Color: " + numberPlateColor;
    }

    private String identifyVehicleType(String numberPlate) {
        if (numberPlate.length() == 10) {
            return "Private Vehicle";
        } else if (numberPlate.length() == 9) {
            return "Commercial Vehicle";
        }
        return "Unknown Vehicle Type";
    }

    private String identifyNumberPlateColor(String vehicleType) {
        switch (vehicleType) {
            case "Private Vehicle":
                return "White (Private)";
            case "Commercial Vehicle":
                return "Yellow (Commercial)";
            default:
                return "Unknown Color";
        }
    }
}
