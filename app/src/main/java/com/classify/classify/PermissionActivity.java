package com.classify.classify;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PermissionActivity extends AppCompatActivity {

    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 99;
    private final static int READ_EXTERNAL_STORAGE_PERMMISSION_RESULT = 0;
    Button permission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        permission = (Button)findViewById(R.id.permission);
        permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkReadExternalStoragePermission();
                checkPermission();
            }
        });
        checkReadExternalStoragePermission();
        checkPermission();
    }
    private void checkReadExternalStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(PermissionActivity.this, MainActivity.class);
                startActivity(i);
            } else {
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs to view thumbnails", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMMISSION_RESULT);
            }
        } else {
            Intent i = new Intent(PermissionActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    private void checkPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;

            default:
                break;
        }
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case READ_EXTERNAL_STORAGE_PERMMISSION_RESULT:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkReadExternalStoragePermission();
                }
                else

                break;


            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
