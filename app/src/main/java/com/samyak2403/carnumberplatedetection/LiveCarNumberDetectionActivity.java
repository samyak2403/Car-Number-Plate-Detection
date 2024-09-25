package com.samyak2403.carnumberplatedetection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.sk365.messagelibrary.ToastMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Data class to store number plate info
class NumberPlateInfo {
    private String numberPlate;
    private String state;
    private String vehicleType;
    private String numberPlateColor;

    public NumberPlateInfo(String numberPlate, String state, String vehicleType, String numberPlateColor) {
        this.numberPlate = numberPlate;
        this.state = state;
        this.vehicleType = vehicleType;
        this.numberPlateColor = numberPlateColor;
    }

    @Override
    public String toString() {
        return "\n--------------------------------------------------"+
                "\nNumber Plate: " + numberPlate +
                "\n--------------------------------------------------"+
                "\nState: " + state +
                "\n--------------------------------------------------"+
                "\nVehicle Type: " + vehicleType +
                "\n--------------------------------------------------"+
                "\nNumber Plate Color: " + numberPlateColor +
                "\n--------------------------------------------------";

    }
}

public class LiveCarNumberDetectionActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 1001;
    private PreviewView previewView;
    private TextView numberPlateTextView;
    private TextRecognizer textRecognizer;
    private boolean isProcessingFrame = false;
    private ExecutorService imageProcessingExecutor;

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
        // Union Territories
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
        setContentView(R.layout.activity_live_car_number_detection);

        previewView = findViewById(R.id.previewView);
        numberPlateTextView = findViewById(R.id.numberPlateTextView);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        imageProcessingExecutor = Executors.newFixedThreadPool(2);  // Multi-threading to handle image processing

        // Check and request camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Set up the preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Select the back camera
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Set up image analysis
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(imageProcessingExecutor, image -> {
                    if (!isProcessingFrame) {
                        isProcessingFrame = true;
                        processImage(image);
                    }
                });

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void processImage(ImageProxy image) {
        if (image == null || image.getImage() == null) {
            image.close();
            isProcessingFrame = false;
            return;
        }

        // Image processing logic
        InputImage inputImage = InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees());

        textRecognizer.process(inputImage)
                .addOnSuccessListener(visionText -> {
                    String detectedText = extractNumberPlateText(visionText);
                    if (!detectedText.isEmpty()) {
                        runOnUiThread(() -> numberPlateTextView.setText(detectedText));
                    }
                })
                .addOnFailureListener(e -> Log.e("MLKit", "Text recognition failed: " + e.getMessage()))
                .addOnCompleteListener(task -> {
                    image.close();
                    isProcessingFrame = false;
                });
    }

    // Updated method to handle bike and car number plate formats
    private String extractNumberPlateText(Text visionText) {
        StringBuilder numberPlateText = new StringBuilder();

        // Updated regex pattern for bikes and cars
        String regexPattern = "([A-Z]{2}\\d{2}\\s?[A-Z]{2}\\d{4})";  // Matches first row (e.g. KA41) and second row (e.g. ER4547)
        Pattern pattern = Pattern.compile(regexPattern);

        for (Text.TextBlock block : visionText.getTextBlocks()) {
            String blockText = block.getText();
            Matcher matcher = pattern.matcher(blockText);
            while (matcher.find()) {
                String numberPlate = matcher.group();
                NumberPlateInfo info = formatNumberPlateDetails(numberPlate);
                numberPlateText.append(info.toString()).append("\n\n");
            }
        }
        return numberPlateText.toString();
    }

    // Format number plate details based on the regex match
    private NumberPlateInfo formatNumberPlateDetails(String numberPlate) {
        String stateCode = numberPlate.substring(0, 2);
        String state = STATE_MAP.getOrDefault(stateCode, "Unknown State");
        String vehicleType = identifyVehicleType(numberPlate);
        String numberPlateColor = identifyNumberPlateColor(vehicleType);

        return new NumberPlateInfo(numberPlate, state, vehicleType, numberPlateColor);
    }

    // Identify vehicle type based on number plate format
    private String identifyVehicleType(String numberPlate) {
        if (numberPlate.length() == 10) {
            return "Private Vehicle";
        } else if (numberPlate.length() == 9) {
            return "Commercial Vehicle";
        } else if (numberPlate.startsWith("DL") || numberPlate.startsWith("MH")) {
            return "Government Vehicle";
        } else if (numberPlate.startsWith("TS")) {
            return "Transport Vehicle";
        } else {
            return "Unknown Vehicle Type";
        }
    }

    // Determine number plate color based on vehicle type
    private String identifyNumberPlateColor(String vehicleType) {
        switch (vehicleType) {
            case "Private Vehicle":
                return "White";
            case "Commercial Vehicle":
                return "Yellow";
            case "Government Vehicle":
                return "Red";
            case "Transport Vehicle":
                return "Green";
            default:
                return "Unknown Color";
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            // Using the library to show a toast message


        }
    }
}
