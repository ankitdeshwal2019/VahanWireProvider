package com.electrom.vahanwireprovider.ragistration;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.electrom.vahanwireprovider.MainActivity;
import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.location_service.GPSTracker;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CustomTextView;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class BeforeLogin extends AppCompatActivity implements View.OnClickListener {

    CustomTextView tvPetrolPump, tvMechanic, tvAbmulance;
    SessionManager sessionManager;
    public static final String TAG =BeforeLogin.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_login);
        initView();
    }

    private void initView() {
        sessionManager = SessionManager.getInstance(this);
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "onCreate: android id :: " + android_id);
        if(android_id.length() > 0)
            sessionManager.setString(SessionManager.DEVICE_ID, android_id);

        tvPetrolPump = findViewById(R.id.tvPetrolPump);
        tvMechanic = findViewById(R.id.tvMechanic);
        tvAbmulance = findViewById(R.id.tvAmbulance);
        tvPetrolPump.setOnClickListener(this);
        tvAbmulance.setOnClickListener(this);
        tvMechanic.setOnClickListener(this);

        if(sessionManager.getString(SessionManager.PROVIDER_MOBILE).length() > 0 &&
                sessionManager.getString(SessionManager.PROVIDER_PIN).length() > 0){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.tvPetrolPump:

                Dexter.withActivity(this).withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {
                                    // do you work now
                                    if (!isMyServiceRunning(GPSTracker.class))
                                        startService(new Intent(getApplicationContext(), GPSTracker.class));
                                        startActivity(new Intent(getApplicationContext(), ProviderLogin.class));
                                }

                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                    // permission is denied permenantly, navigate user to app settings
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .onSameThread()
                        .check();
                break;
            case R.id.tvAmbulance:

                Dexter.withActivity(this).withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {
                                    // do you work now
                                    if (!isMyServiceRunning(GPSTracker.class))
                                        startService(new Intent(getApplicationContext(), GPSTracker.class));
                                        ActionForAll.alertUser("VahanWire", "Under Process", "OK", BeforeLogin.this);
                                }

                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                    // permission is denied permenantly, navigate user to app settings
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .onSameThread()
                        .check();
                break;
            case R.id.tvMechanic:

                Dexter.withActivity(this).withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {
                                    // do you work now
                                    if (!isMyServiceRunning(GPSTracker.class))
                                        startService(new Intent(getApplicationContext(), GPSTracker.class));
                                        ActionForAll.alertUser("VahanWire", "Under Process", "OK", BeforeLogin.this);
                                }

                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .onSameThread()
                        .check();

                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        sessionManager.setString(SessionManager.NOTIFICATION_TOKEN, token);
                        Log.d(TAG, "token" + token);
                        //ActionForAll.myFlash(getApplicationContext(), token);
                    }
                });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BeforeLogin.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}
