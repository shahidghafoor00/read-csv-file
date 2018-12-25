package com.byteshaft.readcsvfile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MULTIPLE_PERMISSIONS = 100;
    private String[] permissions;
    private Button mButtonManualSearch;
    private Button mScanButton;
    private Button mLoadCsvButton;
    private TextView mTextviewFileName;
    private TextView mTextViewModuleNumber;
    private TextView mAbsNumber;
    private TextView mPosition;
    private EditText mInputFieldEAN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        //initialize UI
        mTextviewFileName = findViewById(R.id.textview_filename);
        mTextViewModuleNumber = findViewById(R.id.text_view_module_number);
        mAbsNumber = findViewById(R.id.text_view_abs_number);
        mPosition = findViewById(R.id.text_view_position);
        mInputFieldEAN = findViewById(R.id.edit_text_ean);

        mScanButton = findViewById(R.id.button_scan);
        mLoadCsvButton = findViewById(R.id.button_load_csv);
        mButtonManualSearch = findViewById(R.id.button_manual_search);

        mScanButton.setOnClickListener(this);
        mButtonManualSearch.setOnClickListener(this);
        mLoadCsvButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_manual_search:
                // TODO: 25/12/2018
                break;

            case R.id.button_scan:
                if (checkPermissions()) {
                    startActivity(new Intent(MainActivity.this, ScannerActivity.class));
                }
                break;

            case R.id.button_load_csv:
                if (checkPermissions()) {
                    // TODO: 25/12/2018 open file manager to select csv file
                    // for now csv file is on device  /mnt/sdcard
                    String path = Environment.getExternalStorageDirectory().toString() + "/garten.csv";
                    doSHit(path);
                }
                break;

        }
    }

    private void doSHit(String file) {
        File f = new File(file);
        CsvReader csvReader = new CsvReader();
        csvReader.setFieldSeparator(';');
//        csvReader.setTextDelimiter('\'');
        csvReader.setContainsHeader(true);

        CsvContainer csv;
        try {
            csv = csvReader.read(f, StandardCharsets.UTF_8);
//            List<CsvRow> rows = csv.getRows();
//            Log.e("ok", "doSHit: " + csv.getRows());
            for (CsvRow row : csv.getRows()) {
                if (row.getField("EAN").equals("4306517320003")) {
                    System.out.println(row.getField("Position"));
                    mPosition.setText("Position Number: " + row.getField("Position"));
                    mAbsNumber.setText("Abschnitt Number: " + row.getField("Abschnitt"));
                    mTextViewModuleNumber.setText("Modul Number: " + row.getField("Modul"));
                }
//                System.out.println(row.getFields());
//                System.out.println("First column of line: " + row.getField("EAN"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean checkPermissions() {
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
            return false;
        }
        return true;
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
                    System.out.println("permission granted");
                }
                return;
            }
        }
    }
}
