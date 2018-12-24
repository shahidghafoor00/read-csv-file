package com.byteshaft.readcsvfile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity {

    public static final int MULTIPLE_PERMISSIONS = 100;
    private String[] permissions;

    private ZXingScannerView mScannerView;
    Button mScanButton;
    TextView mTextView;
    private List<Dataset> datasetArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        mTextView = findViewById(R.id.tv);
        mScanButton = findViewById(R.id.button_scan);
        datasetArrayList = new ArrayList<>();

        readWeatherData();
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
    }

    private void checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(
                    new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
        }
    }

    private void readWeatherData() {
        // Read the raw csv file
        InputStream is = getResources().openRawResource(
                getResources().getIdentifier("test",
                        "raw", getPackageName()));
        // Reads text from character-input stream, buffering characters for efficient reading
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        // Initialization
        String line = "";

        // Initialization
        try {
            // Step over headers
            reader.readLine();

            // If buffer is not empty
            while ((line = reader.readLine()) != null) {
                Log.d("MyActivity", "Line: " + line);
                mTextView.setText(line);

                // use comma as separator columns of CSV
                String[] tokens = line.split(";");
                // Read the data
                Dataset sample = new Dataset();

                // Setters
                sample.setEan(tokens[0]);
                sample.setModule(tokens[1]);
                sample.setAbschnitt(tokens[2]);
                sample.setIndexNumber(tokens[3]);

                // Adding object to a class
                datasetArrayList.add(sample);

                // Log the object
                Log.wtf("My Activity", "Just created: " + sample);
            }

        } catch (IOException e) {
            // Logs error with priority level
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);

            // Prints throwable details
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissionsList,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    String permissionsDenied = "";
                    for (String per : permissionsList) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            permissionsDenied += "\n" + per;
                        }
                    }
                    startActivity(new Intent(MainActivity.this, ScannerActivity.class));
                    System.out.println("permission granted");
                }
                return;
            }
        }
    }
}
