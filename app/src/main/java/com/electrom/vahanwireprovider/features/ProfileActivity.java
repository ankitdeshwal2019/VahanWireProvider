package com.electrom.vahanwireprovider.features;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.MainActivity;
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
import com.electrom.vahanwireprovider.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    ImageView iv_Profile_back, iv_profile_image;
    CustomEditText etProfileCompanyName, etProfilePersonName, etProfileMobileNumber,etProfileLandLine,
            etProfileAddress, etProfileCountry, etProfileState, etProfileCity, etProfilePincode;
    CustomButton btnProfileSubmit;
    SessionManager sessionManager;

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
        btnProfileSubmit.setOnClickListener(this);
        iv_Profile_back.setOnClickListener(this);
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
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }, 300);
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
}
