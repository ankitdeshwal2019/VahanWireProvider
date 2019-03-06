package com.electrom.vahanwireprovider.features;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.electrom.vahanwireprovider.MainActivity;
import com.electrom.vahanwireprovider.Multipart.MultipartRequest;
import com.electrom.vahanwireprovider.Multipart.NetworkOperationHelper;
import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.models.detail.Detail;
import com.electrom.vahanwireprovider.models.update_profile.Datum;
import com.electrom.vahanwireprovider.models.update_profile.Update;
import com.electrom.vahanwireprovider.ragistration.RegistrationActivity;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomEditText;
import com.electrom.vahanwireprovider.utility.PicassoClient;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.UrlConstants;
import com.electrom.vahanwireprovider.utility.Util;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    ImageView iv_Profile_back, iv_profile_image, ivProfileContainer;
    CustomEditText etProfileCompanyName, etProfilePersonName, etProfileMobileNumber,etProfileLandLine,
            etProfileAddress, etProfileCountry, etProfileState, etProfileCity, etProfilePincode;
    CustomButton btnProfileSubmit;
    SessionManager sessionManager;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String imgUSerString = "", userId, name, userChoosenTask, mobile, email, progilePic, service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
    }

    private void initView() {
        sessionManager = SessionManager.getInstance(this);
        iv_Profile_back = findViewById(R.id.iv_Profile_back);
        iv_profile_image = findViewById(R.id.iv_profile_image);
        etProfileCompanyName = findViewById(R.id.etProfileCompanyName);
        etProfilePersonName = findViewById(R.id.etProfilePersonName);
        etProfileMobileNumber = findViewById(R.id.etProfileMobileNumber);
        etProfileLandLine = findViewById(R.id.etProfileLandLine);
        etProfileAddress = findViewById(R.id.etProfileAddress);
        etProfileCountry = findViewById(R.id.etProfileCountry);
        etProfileState = findViewById(R.id.etProfileState);
        etProfileCity = findViewById(R.id.etProfileCity);
        etProfilePincode = findViewById(R.id.etProfilePincode);
        btnProfileSubmit = findViewById(R.id.btnProfileSubmit);
        ivProfileContainer = findViewById(R.id.ivProfileContainer);
        btnProfileSubmit.setOnClickListener(this);
        iv_Profile_back.setOnClickListener(this);
        ivProfileContainer.setOnClickListener(this);
        setAllFielda();
    }

    private void setAllFielda() {

        //PicassoClient.downloadImage(this, sessionManager.getString(SessionManager.PROVIDER_IMAGE),iv_profile_image);
        etProfileCompanyName.setText(sessionManager.getString(SessionManager.REGISTER_NAME));
        etProfilePersonName.setText(sessionManager.getString(SessionManager.CONTACT_PERSON));
        etProfileMobileNumber.setText(sessionManager.getString(SessionManager.PROVIDER_MOBILE));
        etProfileLandLine.setText(sessionManager.getString(SessionManager.LANDLINE));
        etProfileAddress.setText(sessionManager.getString(SessionManager.ADDRESS));
        PicassoClient.downloadImage(this, sessionManager.getString(SessionManager.PROVIDER_IMAGE), iv_profile_image);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnProfileSubmit:
                isNotEmptyFields();
                break;

            case R.id.iv_Profile_back:
                finish();
                break;

            case R.id.ivProfileContainer:
                selectImage();
                break;
        }
    }

    private void compaleteRegistration() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Update> call = apiService.registrationUpdate(
                sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                sessionManager.getString(SessionManager.PROVIDER_PIN),
                etProfileCompanyName.getText().toString(),
                etProfilePersonName.getText().toString(),
                etProfileLandLine.getText().toString(),
                "","","","","","",etProfileAddress.getText().toString(),
                "","","","","","","","","","","","",
                "","","","","","","","","","" +
                        "","","","","","","","");

        call.enqueue(new Callback<Update>() {
            @Override
            public void onResponse(Call<Update> call, Response<Update> response) {
                Update update = response.body();
                Log.d(TAG, update.getStatus());

                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);
                    if(update.getStatus().equals("200"))
                    {
                        List<Datum> list = update.getData();
                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, list.get(0).getMobile());
                        sessionManager.setString(SessionManager.PROVIDER_PIN, list.get(0).getMobilePin());
                        sessionManager.setString(SessionManager.REGISTER_NAME, list.get(0).getRegisteredName());
                        sessionManager.setString(SessionManager.EMAIL, list.get(0).getEmail());
                        sessionManager.setString(SessionManager.ADDRESS, list.get(0).getAddress().getFirstAddress());
                        sessionManager.setString(SessionManager.CONTACT_PERSON,list.get(0).getContactPerson());
                        sessionManager.setString(SessionManager.LANDLINE, list.get(0).getPhone());
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, list.get(0).getProfilePic());
                        //PicassoClient.downloadImage(this, sessionManager.getString(SessionManager.PROVIDER_IMAGE),iv_profile_image);
                        etProfileCompanyName.setText(sessionManager.getString(SessionManager.REGISTER_NAME));
                        etProfilePersonName.setText(sessionManager.getString(SessionManager.CONTACT_PERSON));
                        etProfileMobileNumber.setText(sessionManager.getString(SessionManager.PROVIDER_MOBILE));
                        etProfileLandLine.setText(sessionManager.getString(SessionManager.LANDLINE));
                        etProfileAddress.setText(sessionManager.getString(SessionManager.ADDRESS));
                        PicassoClient.downloadImage(getApplicationContext(), sessionManager.getString(SessionManager.PROVIDER_IMAGE), iv_profile_image);
                        ActionForAll.alertUser("VahanWire", update.getMessage(), "OK", ProfileActivity.this);
                    }
                    else
                    {
                        ActionForAll.alertUser("VahanWire", update.getMessage(), "OK", ProfileActivity.this);
                    }

                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", ProfileActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Update> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void isNotEmptyFields(){
        if(ActionForAll.validMobileEditText(etProfileMobileNumber,"mobile Number", ProfileActivity.this))
        {
            compaleteRegistration();
        }
        else
        {
            ActionForAll.alertUserWithCloseActivity("VahanWire", "There is some issue, Try after sometime", "OK", ProfileActivity.this);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            ActionForAll.alertChoiseCloseActivity(this);
        }
        return true;
    }

    // upload

    private void selectImage() {
        final CharSequence[] items = {"Camera", "Choose from Gallery",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result = Utility.checkPermission(getActivity());

                if (items[item].equals("Camera")) {
                    userChoosenTask = "Camera";
                    //if (result)
                    //cameraIntent();
                    requestCameraPermission();

                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    //if (result)
                    //galleryIntent();
                    requestStoragePermission();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);

            if(data!=null)
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        profileUpdate();
                    }
                },300);

            }
            else
            {
              ActionForAll.alertUser("VawanWire", "No Image found", "OK", ProfileActivity.this);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        iv_profile_image.setImageBitmap(bm);
        getImageUri(this, bm);

    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        iv_profile_image.setImageBitmap(thumbnail);
        getImageUri(this, thumbnail);
    }

    private void profileUpdate() {
       final ProgressDialog pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request

        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
        MultipartRequest req = null;
        HashMap<String, String> map = new HashMap<>();
        map.put("mobile", sessionManager.getString(SessionManager.PROVIDER_MOBILE));
        service = sessionManager.getString(SessionManager.SERVICE);
        String URL = "";

        if(service.equalsIgnoreCase("Petrol_Pump"))
        {
            URL = UrlConstants.BASE_URL + UrlConstants.PETROL_PUMP_UPLOAD;
        }

        if(service.equalsIgnoreCase("MechanicPro"))
        {
            URL = UrlConstants.BASE_URL + UrlConstants.MECHANIC_UPLOAD;
        }

        try {
            req = new MultipartRequest(URL, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", error.getMessage());
                    return;
                }
            }, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pDialog.dismiss();
                    try {
                        JSONObject object = new JSONObject(response);


                        if(object.getString("status").equals("200"))
                        {
                            JSONObject data = object.getJSONObject("data");
                            Log.e(TAG, "onResponse: " +  data.getString("profile_pic"));
                            sessionManager.setString(SessionManager.PROVIDER_IMAGE, data.getString("profile_pic"));
                            ActionForAll.alertUser("VahanWire", object.getString("message"), "OK", ProfileActivity.this);
                        }
                        else
                        {
                            ActionForAll.alertUser("VahanWire", object.getString("message"), "OK", ProfileActivity.this);
                        }
                        Log.e("resMulti", response.toString());
                        }

                     catch (JSONException e) {
                         ActionForAll.alertUser("VahanWire", "error  ", "OK", ProfileActivity.this);
                        pDialog.dismiss();
                        Log.e("JSONException", e.toString());
                        e.printStackTrace();
                    }
                }
            }, map);

            req.addImageData("profile_pic", new File(imgUSerString));
            NetworkOperationHelper.getInstance(this).addToRequestQueue(req);

        } catch (UnsupportedEncodingException e) {
            pDialog.dismiss();


            e.printStackTrace();
        } catch (NullPointerException e) {
            pDialog.dismiss();

            e.printStackTrace();
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(Uri.parse(path), filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        imgUSerString = cursor.getString(columnIndex);
        Log.e(TAG, "getImageUri: "+ path);
        return Uri.parse(path);
    }

    private void requestCameraPermission() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            openCamera();

                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        //Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                        ActionForAll.myFlash(getApplicationContext(), "Error occurred!");
                    }
                })
                .onSameThread()
                .check();
    }

    private void requestStoragePermission() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //ActionForAll.myFlash(getApplicationContext(), "All permissions are granted!");
                            galleryIntent();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        //Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                        ActionForAll.myFlash(getApplicationContext(), "Error occurred!");
                    }
                })
                .onSameThread()
                .check();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

}
