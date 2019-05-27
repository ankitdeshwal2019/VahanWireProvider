package com.electrom.vahanwireprovider.features;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.electrom.vahanwireprovider.Multipart.MultipartRequest;
import com.electrom.vahanwireprovider.Multipart.NetworkOperationHelper;
import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.adapters.CityAdapter;
import com.electrom.vahanwireprovider.adapters.SpinnerAdapter;
import com.electrom.vahanwireprovider.adapters.StateAdapter;
import com.electrom.vahanwireprovider.models.city.City;
import com.electrom.vahanwireprovider.models.city.CityData;
import com.electrom.vahanwireprovider.models.country.Country;
import com.electrom.vahanwireprovider.models.new_petrol_pump_detail.NewPetrolDetail;
import com.electrom.vahanwireprovider.models.pro_update_mech.ProfileUpdateMech;
import com.electrom.vahanwireprovider.models.state.DateState;
import com.electrom.vahanwireprovider.models.state.State;
import com.electrom.vahanwireprovider.models.update_profile.Datum;
import com.electrom.vahanwireprovider.models.update_profile.Update;
import com.electrom.vahanwireprovider.ragistration.ProviderLogin;
import com.electrom.vahanwireprovider.ragistration.RegistrationActivity;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.Constant;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomEditText;
import com.electrom.vahanwireprovider.utility.PicassoClient;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.UrlConstants;
import com.electrom.vahanwireprovider.utility.Util;
import com.fasterxml.jackson.databind.util.Provider;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    ImageView iv_Profile_back, iv_profile_image, ivProfileContainer;
    CustomEditText etProfileCompanyName, etProfilePersonName, etProfileMobileNumber,
            etProfileLandLine, etProfileAddress, etPinCode;
    CustomButton btnProfileSubmit;
    SessionManager sessionManager;
    String country, state_name, city_name, pincode = "";
    CustomEditText spinCountry, spinState, spinCity;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String imgUSerString = "", userChoosenTask, service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
    }

    private void initView() {
        //getCountry();
        sessionManager = SessionManager.getInstance(this);
        iv_Profile_back = findViewById(R.id.iv_Profile_back);
        iv_profile_image = findViewById(R.id.iv_profile_image);
        etProfileCompanyName = findViewById(R.id.etProfileCompanyName);
        etProfilePersonName = findViewById(R.id.etProfilePersonName);
        etProfileMobileNumber = findViewById(R.id.etProfileMobileNumber);
        etProfileLandLine = findViewById(R.id.etProfileLandLine);
        etProfileAddress = findViewById(R.id.etProfileAddress);
        spinCountry = findViewById(R.id.etCountry);
        spinState = findViewById(R.id.etState);
        spinCity = findViewById(R.id.etCity);
        //etProfilePincode = findViewById(R.id.etProfilePincode);
        btnProfileSubmit = findViewById(R.id.btnProfileSubmit);
        ivProfileContainer = findViewById(R.id.ivProfileContainer);
        etPinCode = findViewById(R.id.etPinCode);
        btnProfileSubmit.setOnClickListener(this);
        iv_Profile_back.setOnClickListener(this);
        ivProfileContainer.setOnClickListener(this);
        setAllFielda();

    }

    private void setAllFielda() {

        etProfileCompanyName.setText(sessionManager.getString(SessionManager.CONTACT_PERSON));
        etProfilePersonName.setText(sessionManager.getString(SessionManager.REGISTER_NAME));
        etProfileMobileNumber.setText(sessionManager.getString(SessionManager.PROVIDER_MOBILE));
        etProfileLandLine.setText(sessionManager.getString(SessionManager.LANDLINE));
        etProfileAddress.setText(sessionManager.getString(SessionManager.ADDRESS));
        spinCity.setText(sessionManager.getString(SessionManager.CITY));
        spinState.setText(sessionManager.getString(SessionManager.STATE));
        spinCountry.setText(sessionManager.getString(SessionManager.COUNRTY));
        etPinCode.setText(sessionManager.getString(SessionManager.PINCODE));
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
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
                {
                    selectImageBelowMarse();
                }
                else
                {
                    selectImage();
                }
                //selectImage();
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
                spinCity.getText().toString(), spinState.getText().toString(), spinCountry.getText().toString(), etPinCode.getText().toString(),"","","","","","","","",
                "","","","","","","","","","" +
                        "","","","","","","","");


        Log.e(TAG, "compaleteRegistration: mob "+  sessionManager.getString(SessionManager.PROVIDER_MOBILE));
        Log.e(TAG, "compaleteRegistration: pin "+  sessionManager.getString(SessionManager.PROVIDER_PIN));
        Log.e(TAG, "compaleteRegistration: name "+  etProfileCompanyName.getText().toString());
        Log.e(TAG, "compaleteRegistration: penson  "+   etProfilePersonName.getText().toString());
        Log.e(TAG, "compaleteRegistration: address "+  etProfileAddress.getText().toString());
        Log.e(TAG, "compaleteRegistration: "+  city_name);
        Log.e(TAG, "compaleteRegistration: "+  state_name);
        Log.e(TAG, "compaleteRegistration: "+  country);
        Log.e(TAG, "compaleteRegistration: "+  etPinCode.getText().toString());



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
                        sessionManager.setString(SessionManager.CONTACT_PERSON, list.get(0).getRegisteredName());
                        sessionManager.setString(SessionManager.EMAIL, list.get(0).getEmail());
                        sessionManager.setString(SessionManager.ADDRESS, list.get(0).getAddress().getFirstAddress());
                        sessionManager.setString(SessionManager.REGISTER_NAME,list.get(0).getContactPerson());
                        sessionManager.setString(SessionManager.LANDLINE, list.get(0).getPhone());
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, list.get(0).getProfilePic());
                        sessionManager.setString(SessionManager.COUNRTY, list.get(0).getAddress().getCountry());
                        sessionManager.setString(SessionManager.STATE, list.get(0).getAddress().getState());
                        sessionManager.setString(SessionManager.CITY, list.get(0).getAddress().getCity());
                        sessionManager.setString(SessionManager.PINCODE, list.get(0).getAddress().getPincode());
                        etProfileCompanyName.setText(sessionManager.getString(SessionManager.CONTACT_PERSON));
                        etProfilePersonName.setText(sessionManager.getString(SessionManager.REGISTER_NAME));
                        etProfileMobileNumber.setText(sessionManager.getString(SessionManager.PROVIDER_MOBILE));
                        etProfileLandLine.setText(sessionManager.getString(SessionManager.LANDLINE));
                        etProfileAddress.setText(sessionManager.getString(SessionManager.ADDRESS));
                        etPinCode.setText(sessionManager.getString(SessionManager.PINCODE));
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

    private void compaleteRegistrationNew() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<NewPetrolDetail> call = apiService.registrationUpdateNew(
                sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                sessionManager.getString(SessionManager.PROVIDER_PIN),
                etProfileCompanyName.getText().toString(),
                etProfilePersonName.getText().toString(),
                etProfileLandLine.getText().toString(),
                "","","","","","",
                etProfileAddress.getText().toString(),
                spinCity.getText().toString(),
                spinState.getText().toString(),
                spinCountry.getText().toString(),
                etPinCode.getText().toString(),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "","","");


        Log.e(TAG, "compaleteRegistration: mob "+  sessionManager.getString(SessionManager.PROVIDER_MOBILE));
        Log.e(TAG, "compaleteRegistration: pin "+  sessionManager.getString(SessionManager.PROVIDER_PIN));
        Log.e(TAG, "compaleteRegistration: name "+  etProfileCompanyName.getText().toString());
        Log.e(TAG, "compaleteRegistration: penson  "+   etProfilePersonName.getText().toString());
        Log.e(TAG, "compaleteRegistration: address "+  etProfileAddress.getText().toString());
        Log.e(TAG, "compaleteRegistration: "+  city_name);
        Log.e(TAG, "compaleteRegistration: "+  state_name);
        Log.e(TAG, "compaleteRegistration: "+  country);
        Log.e(TAG, "compaleteRegistration: "+  etPinCode.getText().toString());



        call.enqueue(new Callback<NewPetrolDetail>() {
            @Override
            public void onResponse(Call<NewPetrolDetail> call, Response<NewPetrolDetail> response) {
                NewPetrolDetail update = response.body();
                Log.d(TAG, update.getStatus());

                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);
                    if(update.getStatus().equals("200"))
                    {
                        List<com.electrom.vahanwireprovider.models.new_petrol_pump_detail.Datum> list = update.getData();
                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, list.get(0).getMobile());
                        sessionManager.setString(SessionManager.PROVIDER_PIN, list.get(0).getMobilePin());
                        sessionManager.setString(SessionManager.CONTACT_PERSON, list.get(0).getRegisteredName());
                        sessionManager.setString(SessionManager.EMAIL, list.get(0).getEmail());
                        sessionManager.setString(SessionManager.ADDRESS, list.get(0).getAddress().getFirstAddress());
                        sessionManager.setString(SessionManager.REGISTER_NAME,list.get(0).getContactPerson());
                        sessionManager.setString(SessionManager.LANDLINE, list.get(0).getPhone());
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, list.get(0).getProfilePic());
                        sessionManager.setString(SessionManager.COUNRTY, list.get(0).getAddress().getCountry());
                        sessionManager.setString(SessionManager.STATE, list.get(0).getAddress().getState());
                        sessionManager.setString(SessionManager.CITY, list.get(0).getAddress().getCity());
                        sessionManager.setString(SessionManager.PINCODE, list.get(0).getAddress().getPincode());
                        etProfileCompanyName.setText(sessionManager.getString(SessionManager.CONTACT_PERSON));
                        etProfilePersonName.setText(sessionManager.getString(SessionManager.REGISTER_NAME));
                        etProfileMobileNumber.setText(sessionManager.getString(SessionManager.PROVIDER_MOBILE));
                        etProfileLandLine.setText(sessionManager.getString(SessionManager.LANDLINE));
                        etProfileAddress.setText(sessionManager.getString(SessionManager.ADDRESS));
                        etPinCode.setText(sessionManager.getString(SessionManager.PINCODE));
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
            public void onFailure(Call<NewPetrolDetail> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    private void compaleteRegistrationMechanic() {
        Log.e(TAG, "service: regstration for mechanic ");
        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ProfileUpdateMech> call = apiService.registrationUpdateMechanic(
                sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                etProfilePersonName.getText().toString(),
                "",
                etProfileCompanyName.getText().toString(),
                sessionManager.getString(SessionManager.LATITUDE),
                sessionManager.getString(SessionManager.LONGITUDE),
                "",
                "",
                "",
                etProfileLandLine.getText().toString(),
                etProfileAddress.getText().toString(),
                spinCity.getText().toString(), spinState.getText().toString(), spinCountry.getText().toString(), etPinCode.getText().toString());


        call.enqueue(new Callback<ProfileUpdateMech>() {
            @Override
            public void onResponse(Call<ProfileUpdateMech> call, Response<ProfileUpdateMech> response) {

                final ProfileUpdateMech detail = response.body();
                if(response.isSuccessful())
                {
                   if(detail.getStatus().equals("200"))
                   {

                       detail.getData().getName();
                       new Handler().postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               Util.hideProgressDialog(progressDialog);

                               sessionManager.setString(SessionManager.PROVIDER_MOBILE, detail.getData().getMobile());
                               sessionManager.setString(SessionManager.PROVIDER_PIN, detail.getData().getMobilePin());
                               sessionManager.setString(SessionManager.REGISTER_NAME, detail.getData().getName());
                               sessionManager.setString(SessionManager.EMAIL, detail.getData().getOrganisation().getEmail());
                               sessionManager.setString(SessionManager.ADDRESS, detail.getData().getOrganisation().getRegAddress().getFirstAddress());
                               sessionManager.setString(SessionManager.CONTACT_PERSON,detail.getData().getOrganisation().getOrganisationName());
                               sessionManager.setString(SessionManager.LANDLINE, detail.getData().getOrganisation().getPhone());
                               sessionManager.setString(SessionManager.PROVIDER_IMAGE, detail.getData().getProfilePic());
                               sessionManager.setString(SessionManager.PROVIDER_ID, detail.getData().getId());
                               sessionManager.setString(SessionManager.COUNRTY, detail.getData().getOrganisation().getRegAddress().getCountry());
                               sessionManager.setString(SessionManager.STATE, detail.getData().getOrganisation().getRegAddress().getState());
                               sessionManager.setString(SessionManager.CITY, detail.getData().getOrganisation().getRegAddress().getCity());
                               sessionManager.setString(SessionManager.PINCODE, detail.getData().getOrganisation().getRegAddress().getPincode());

                              /* Intent logout= new Intent(ProfileActivity.this, ProfileActivity.class);
                               logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(logout);*/
                               ActionForAll.alertUser("VahanWire", "Profile updated Mechanic", "OK", ProfileActivity.this);
                           }
                       }, 300);
                   }
                   else
                   {
                       ActionForAll.alertUser("VahanWire", detail.getMessage(), "OK", ProfileActivity.this);
                   }
                }
            }

            @Override
            public void onFailure(Call<ProfileUpdateMech> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUserWithCloseActivity("VahanWire", t.getMessage(), "OK", ProfileActivity.this);
            }
        });
    }

    private void isNotEmptyFields(){
        if(ActionForAll.validEditText(etProfileCompanyName,"name", ProfileActivity.this)
                && ActionForAll.validEditText(etProfilePersonName,"name", ProfileActivity.this)
                && ActionForAll.validMobileEditText(etProfileMobileNumber,"mobile Number", ProfileActivity.this)
                && ActionForAll.validEditText(etProfileAddress,"address", ProfileActivity.this)
                && ActionForAll.validEditText(spinCountry, "country", ProfileActivity.this)
                && ActionForAll.validEditText(spinState, "state", ProfileActivity.this)
                && ActionForAll.validEditText(spinCity, "city", ProfileActivity.this)
                && ActionForAll.validEditText(etPinCode, "pincode", ProfileActivity.this))

        {
            if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_PETROL_PUMP))
            {
                Log.e(TAG, "isNotEmptyFields: " + sessionManager.getString(SessionManager.SERVICE));
                //compaleteRegistration();
                compaleteRegistrationNew();
            }
            else  if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_AMBULANCE))
            {
                Log.e(TAG, "isNotEmptyFields: " + sessionManager.getString(SessionManager.SERVICE));
            }
            else if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_MECHNIC_PRO))
            {
                compaleteRegistrationMechanic();
                Log.e(TAG, "isNotEmptyFields: " + sessionManager.getString(SessionManager.SERVICE));
            }
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

    private void selectImageBelowMarse() {

        final CharSequence[] items = {"Camera",
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
                }

                else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    public static Uri handleImageUri(Uri uri) {
        if (uri.getPath().contains("content")) {
            Pattern pattern = Pattern.compile("(content://media/.*\\d)");
            Matcher matcher = pattern.matcher(uri.getPath());
            if (matcher.find())
                return Uri.parse(matcher.group(1));
            else
                throw new IllegalArgumentException("Cannot handle this URI");
        }
        return uri;
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

        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            getRealPathFromURI(this, data.getData());
        } else {
            getImageUri(this, thumbnail);
        }
    }
    public String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            Uri newUri = handleImageUri(uri);
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(newUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            imgUSerString =  cursor.getString(column_index);
            Log.e(TAG, "getRealPathFromURI: " +  cursor.getString(column_index));
            return cursor.getString(column_index);
        } catch (Exception e){
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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
                        Log.e(TAG, "run: " + imgUSerString);

                        if(imgUSerString.contains("jpg") || imgUSerString.contains("png") || imgUSerString.contains("jpeg"))
                            profileUpdate();
                        else
                        {
                            PicassoClient.downloadImage(ProfileActivity.this, sessionManager.getString(SessionManager.PROVIDER_IMAGE), iv_profile_image);
                            ActionForAll.alertUser("VahanWire", "The selected image can not be uploaded.", "OK", ProfileActivity.this);
                        }
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
                    ActionForAll.alertUserWithCloseActivity("VahanVire", "The selected image can not be uploaded.", "OK", ProfileActivity.this);
                    if(pDialog.isShowing())
                        pDialog.dismiss();
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
                            ActionForAll.alertUser("VahanWire", "Profile image updated successfully", "OK", ProfileActivity.this);
                        }
                        else
                        {
                            ActionForAll.alertUser("VahanWire", object.getString("message"), "OK", ProfileActivity.this);
                        }
                        Log.e("resMulti", response);
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
            //e.printStackTrace();
        } catch (NullPointerException e) {
            pDialog.dismiss();
            e.printStackTrace();
            ActionForAll.alertUserWithCloseActivity("VahanVire", "The selected image can not be uploaded.", "OK", ProfileActivity.this);
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

   /* private void getCountry(){

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Country> call = apiService.getCounrty();
        call.enqueue(new Callback<Country>() {
            @Override
            public void onResponse(Call<Country> call, Response<Country> response) {

                Util.hideProgressDialog(progressDialog);

                Country service = response.body();

                if(response.isSuccessful())
                {

                    if(service.getStatus().equals("200"))
                    {
                        final List<com.electrom.vahanwireprovider.models.country.DriverData> list  = service.getData();
                        Log.d("size", "list size " + list.size()+"");

                        // CountryAdapter adapter = new CountryAdapter(ProfileActivity.this, list);
                        SpinnerAdapter adapter = new SpinnerAdapter(ProfileActivity.this, list);
                        spinCountry.setAdapter(adapter);
                        for(com.electrom.vahanwireprovider.models.country.DriverData datum : list)
                        {
                            if(datum.getName().equals(sessionManager.getString(SessionManager.COUNRTY)))
                            {
                                country = datum.getName();
                                spinCountry.setSelection(spinCountry.getSelectedItemPosition());
                            }
                        }
                        spinCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Log.e(TAG, "onItemSelected: "+ list.get(position).getCountryCode());
                                country = list.get(position).getName();
                                sessionManager.setInt(SessionManager.COUNRTY_TAG, position);
                                getState(list.get(position).getCountryCode());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    else
                    {
                        country = "";
                    }

                }
                else
                {
                    country ="";
                }

                Log.e("service", response.body().getStatus());

            }

            @Override
            public void onFailure(Call<Country> call, Throwable t) {
                Log.e("service failier", t.getMessage());
                country ="";
            }
        });

    }

    private void getState(String id){

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("country_code", id);


        Call<State> call = apiService.getState(params);
        call.enqueue(new Callback<State>() {
            @Override
            public void onResponse(Call<State> call, Response<State> response) {

                Util.hideProgressDialog(progressDialog);

                State state = response.body();

                if(response.isSuccessful())
                {

                    if(state.getStatus().equals("200"))
                    {

                        final List<DateState> list  = state.getData();
                        Log.d("size", "list size " + list.size()+"");

                        // CountryAdapter adapter = new CountryAdapter(ProfileActivity.this, list);
                        StateAdapter adapter = new StateAdapter(ProfileActivity.this, list);
                        spinState.setAdapter(adapter);

                        for(DateState states : list)
                        {
                            if(states.getName().equals(sessionManager.getString(SessionManager.STATE)))
                            {
                                state_name = states.getName();
                                spinState.setSelection(spinCountry.getSelectedItemPosition());
                            }
                        }
                        spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Log.e(TAG, "onItemSelected: "+ list.get(position).getStateCode());
                                state_name = list.get(position).getName();
                                sessionManager.setInt(SessionManager.STATE_TAG, position);
                                getCity(list.get(position).getStateCode());

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    else
                    {
                        state_name = "";
                    }

                }
                else
                {
                    state_name ="";
                }

                Log.e("service", response.body().getStatus());

            }

            @Override
            public void onFailure(Call<State> call, Throwable t) {
                Log.e("service failier", t.getMessage());
                state_name="";
            }
        });

    }


    private void getCity(String id){

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("state_code", id);

        Call<City> call = apiService.getCity(params);
        call.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, Response<City> response) {

                Util.hideProgressDialog(progressDialog);

                City city = response.body();

                if(response.isSuccessful())
                {
                    com.electrom.vahanwireprovider.models.country.DriverData datum = new com.electrom.vahanwireprovider.models.country.DriverData();


                    if(city.getStatus().equals("200"))
                    {
                        final List<CityData> list  = city.getData();
                        Log.d("size", "list size " + list.size()+"");

                        // CountryAdapter adapter = new CountryAdapter(ProfileActivity.this, list);
                        CityAdapter adapter = new CityAdapter(ProfileActivity.this, list);
                        spinCity.setAdapter(adapter);

                        for(CityData data : list)
                        {
                            city_name = data.getName();
                            if(data.getName().equals(sessionManager.getString(SessionManager.CITY)))
                            {
                                spinState.setSelection(spinCountry.getSelectedItemPosition());
                            }
                        }

                        spinCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Log.e(TAG, "onItemSelected: "+ list.get(position).getStateCode());
                                sessionManager.setInt(SessionManager.CITY_TAG, position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    else
                    {
                        city_name = "";
                    }

                }
                else
                {
                    city_name ="";
                }

                Log.e("service", response.body().getStatus());

            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
                Log.e("service failier", t.getMessage());
                city_name ="";
            }
        });

    }*/

}
