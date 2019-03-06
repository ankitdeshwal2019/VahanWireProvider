package com.electrom.vahanwireprovider.ragistration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.location_service.GPSTracker;
import com.electrom.vahanwireprovider.models.update_profile.Update;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomEditText;
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

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName() ;
    CustomButton btnRegisterFinal;
    SessionManager sessionManager;
    CustomEditText et_resister_com_name,etContactPerson,etRegisterLandLine,etRegisterEmail,
            etRegisterPassword,etRegisterAddress;
    GPSTracker gps;
    Double Latitude, Longitude;
    String service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initView();
    }

    private void getLocation() {
        gps = new GPSTracker(getApplicationContext(), RegistrationActivity.this);
        //Check if GPS enabled
        if (gps.canGetLocation()) {
            Latitude = gps.getLatitude();
            Longitude = gps.getLongitude();
            Log.d("onClick: ", Latitude.toString());
            Log.d("onClick: ", Longitude.toString());
            if (Latitude > 0.1 && Longitude > 0.1) {
                Log.d(TAG, "location found " + Longitude + "/" + Latitude);
                sessionManager.setString(SessionManager.LONGITUDE, Longitude + "");
                sessionManager.setString(SessionManager.LATITUDE, Latitude + "");
            }

        }
    }

    private void initView() {
        sessionManager = SessionManager.getInstance(this);
        btnRegisterFinal = findViewById(R.id.btnRegisterFinal);
        et_resister_com_name = findViewById(R.id.et_resister_com_name);
        etContactPerson = findViewById(R.id.etContactPerson);
        etRegisterLandLine = findViewById(R.id.etRegisterLandLine);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterAddress = findViewById(R.id.etRegisterAddress);
        service = sessionManager.getString(SessionManager.SERVICE);
        Log.e(TAG, "service: registration Act " + service );
        getLocation();
        btnRegisterFinal.setOnClickListener(this);
        etRegisterAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(sessionManager.getString(SessionManager.LONGITUDE).length() == 0 &&
                        sessionManager.getString(SessionManager.LATITUDE).length() == 0)
                {
                    try {
                        if(s.length() > 2)
                            getLocationFromAddress(s.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{}

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnRegisterFinal:

                isNotEmptyFields();
        }
    }

    private void compaleteRegistration() {
        Log.e(TAG, "service: regstration for petrol ");
        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Update> call = apiService.registrationUpdate(
                sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                etRegisterPassword.getText().toString().trim(), et_resister_com_name.getText().toString(),
                etContactPerson.getText().toString(),etRegisterLandLine.getText().toString(),
                etRegisterEmail.getText().toString(), "", "", "1",
                sessionManager.getString(SessionManager.NOTIFICATION_TOKEN),"",
                etRegisterAddress.getText().toString(), "", "","","",
                sessionManager.getString(SessionManager.LATITUDE),sessionManager.getString(SessionManager.LONGITUDE),
                "", "", "","","","","",
                "", "","","","","","",
                "","","","","","","",
                "", "");

        call.enqueue(new Callback<Update>() {
            @Override
            public void onResponse(Call<Update> call, Response<Update> response) {
                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);
                    Update update = response.body();
                        Log.d(TAG, update.getStatus());
                        if(update.getStatus().equals("200"))
                        {
                            //sessionManager.setString(SessionManager.PROVIDER_PIN, etRegisterPassword.getText().toString());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent logout= new Intent(RegistrationActivity.this, ProviderLogin.class);
                                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(logout);
                                }
                            }, 300);
                        }
                        else
                        {
                            ActionForAll.alertUser("VahanWire", update.getMessage(), "OK", RegistrationActivity.this);
                        }

                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", RegistrationActivity.this);
                }

            }

            @Override
            public void onFailure(Call<Update> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void compaleteRegistrationMechanic() {
        Log.e(TAG, "service: regstration for mechanic ");
        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.registrationUpdateMechanic(
                sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                etContactPerson.getText().toString(),
                etRegisterPassword.getText().toString(),
                et_resister_com_name.getText().toString(),
                sessionManager.getString(SessionManager.LATITUDE),
                sessionManager.getString(SessionManager.LONGITUDE),
                etRegisterEmail.getText().toString(),
                "",
                "",
                etRegisterLandLine.getText().toString(),
                etRegisterAddress.getText().toString(),
                "", "", "", "");


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {



                ResponseBody responseBody = response.body();

                try {
                    JSONObject obj = new JSONObject(responseBody.string());
                    obj.getString("status");
                    Log.e(TAG, "onResponse: "+  obj.getString("status"));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Util.hideProgressDialog(progressDialog);
                            sessionManager.setString(SessionManager.PROVIDER_PIN, etRegisterPassword.getText().toString());
                            Intent logout= new Intent(RegistrationActivity.this, ProviderLogin.class);
                            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(logout);
                        }
                    }, 300);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUserWithCloseActivity("VahanWire", t.getMessage(), "OK", RegistrationActivity.this);
            }
        });
    }

    private void isNotEmptyFields(){
        if(ActionForAll.validEditText(et_resister_com_name, "company name", RegistrationActivity.this) &&
                ActionForAll.validEditText(etContactPerson, "name", RegistrationActivity.this) &&
                ActionForAll.isValidEmail(etRegisterEmail, RegistrationActivity.this) &&
                ActionForAll.validEditText(etRegisterPassword, "password", RegistrationActivity.this) &&
                ActionForAll.validEditText(etRegisterAddress, "address", RegistrationActivity.this))
        {
            if(sessionManager.getString(SessionManager.SERVICE).equalsIgnoreCase("Petrol_Pump"))
            {
                compaleteRegistration();
            }
            else if (sessionManager.getString(SessionManager.SERVICE).equalsIgnoreCase("MechanicPro"))
            {
                compaleteRegistrationMechanic();
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


    public void getLocationFromAddress(String strAddress) throws Exception
    {
        //Create coder with Activity context - this
        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            //Get latLng from String
            address = coder.getFromLocationName(strAddress,5);
            //check for null
            if (address == null) {
                return;
            }
            //Lets take first possibility from the all possibilities.
            Address location = address.get(0);
            Log.d(TAG, "d_lat" + location.getLatitude());
            Log.d(TAG, "d_lng" + location.getLongitude());
            sessionManager.setString(SessionManager.LONGITUDE, location.getLongitude() + "");
            sessionManager.setString(SessionManager.LATITUDE, location.getLatitude()+ "");

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
