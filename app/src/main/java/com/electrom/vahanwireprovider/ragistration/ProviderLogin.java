package com.electrom.vahanwireprovider.ragistration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.MainActivity;
import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.features.AmbulanceProvider;
import com.electrom.vahanwireprovider.features.MachanicHomePage;
import com.electrom.vahanwireprovider.models.login.Login;
import com.electrom.vahanwireprovider.models.login_ambulance.Data;
import com.electrom.vahanwireprovider.models.login_ambulance.LoginAmbulance;
import com.electrom.vahanwireprovider.models.mechanic.MechanicLogin;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomEditText;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderLogin extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ProviderLogin.class.getSimpleName();
    CustomButton btnRegister, btnLogin;
    CustomEditText etLoginMobile, etLoginPassword;
    SessionManager sessionManager;
    String service = "";
    ImageView ivBackLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        sessionManager = SessionManager.getInstance(this);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        etLoginMobile = findViewById(R.id.etLoginMobile);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        ivBackLogin = findViewById(R.id.ivBackLogin);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        ivBackLogin.setOnClickListener(this);
        service = sessionManager.getString(SessionManager.SERVICE);
        Log.e(TAG, "service: provider login " + service );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (ActionForAll.validMobileEditText(etLoginMobile, "mobile number", ProviderLogin.this) &&
                        ActionForAll.validEditText(etLoginPassword, "valid pin", ProviderLogin.this)) {

                    Log.e(TAG, "onClick: service  " + service );

                    if(service.contains("Petrol_Pump"))
                    {
                        login();
                    }
                    else if(service.contains("Ambulance"))
                    {
                        loginAmbulance();
                    }

                    else if(service.contains("MechanicPro"))
                    {
                        loginMecanic();
                    }
                    else
                    {
                        ActionForAll.myFlash(getApplicationContext(), "service choice not found");
                    }
                }
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;

            case R.id.btnRegister:
                startActivity(new Intent(getApplicationContext(), RegisterMobile.class));
                break;

            case R.id.ivBackLogin:
                finish();
                break;

        }
    }

    private void loginMecanic() {

        Log.d(TAG, "loginMecanic: device_id " + sessionManager.getString(SessionManager.DEVICE_ID));
        Log.d(TAG, "loginMecanic: device_type " + "1");
        Log.d(TAG, "loginMecanic: notification_id " + sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));
        Log.d(TAG, "loginMecanic: mobile " + etLoginMobile.getText().toString());


        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<MechanicLogin> call = apiService.login_mechanic(etLoginMobile.getText().toString(),
                etLoginPassword.getText().toString(),
                sessionManager.getString(SessionManager.DEVICE_ID),
                "1",
                sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));


        call.enqueue(new Callback<MechanicLogin>() {
            @Override
            public void onResponse(Call<MechanicLogin> call, Response<MechanicLogin> response) {
                if (response != null && response.isSuccessful()) {
                    Log.d(TAG, "onResponse: "+ response.body().getStatus());
                    Util.hideProgressDialog(progressDialog);
                    MechanicLogin login = response.body();
                    if (login.getStatus().equals("200"))
                    {
                        Log.d(TAG, "onResponse: " + "  message " + login.getMessage());
                        Log.d(TAG, "onResponse: " + "  mobile " + login.getData().getMobile());
                        Log.d(TAG, "onResponse: " + "  pin " + "--" + login.getData().getMobilePin());
                        Log.d(TAG, "onResponse: " + " first address " + "--" + login.getData().getOrganisation().getRegAddress().getFirstAddress());
                        Log.d(TAG, "onResponse: " + "contact person " + "--" + login.getData().getOrganisation().getContactPerson());
                        Log.d(TAG, "onResponse: " + "phone number" + "--" + login.getData().getOrganisation().getMobile());
                        List<Double> list = login.getData().getLocation().getCoordinates();
                        Double longitude = list.get(0).doubleValue();
                        Double latitude = list.get(1).doubleValue();
                        Log.d(TAG, "onResponse: " + "longitude" + "--" + longitude);
                        Log.d(TAG, "onResponse: " + "longitude" + "--" + latitude);
                        Log.d(TAG, "onResponse: " + " image " + "--" + login.getData().getProfilePic());

                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, login.getData().getMobile());
                        sessionManager.setString(SessionManager.PROVIDER_PIN, login.getData().getMobilePin());
                        sessionManager.setString(SessionManager.REGISTER_NAME, login.getData().getOrganisation().getOrganisationName());
                        sessionManager.setString(SessionManager.EMAIL, login.getData().getOrganisation().getEmail());
                        sessionManager.setString(SessionManager.ADDRESS, login.getData().getOrganisation().getRegAddress().getFirstAddress());
                        sessionManager.setString(SessionManager.CONTACT_PERSON,login.getData().getOrganisation().getContactPerson());
                        sessionManager.setString(SessionManager.LANDLINE, login.getData().getOrganisation().getPhone());
                        sessionManager.setString(SessionManager.LATITUDE, latitude+"");
                        sessionManager.setString(SessionManager.LONGITUDE, longitude+"");
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, login.getData().getProfilePic());
                        sessionManager.setString(SessionManager.PROVIDER_ID, login.getData().getId());

                        Intent logout= new Intent(getApplicationContext(), MachanicHomePage.class);
                        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logout);
                        finish();
                    }

                    else {
                        Util.hideProgressDialog(progressDialog);
                        ActionForAll.alertUserWithCloseActivity("VahanWire", login.getMessage(), "OK", ProviderLogin.this);
                    }

                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", ProviderLogin.this);
                }
            }

            @Override
            public void onFailure(Call<MechanicLogin> call, Throwable t) {
                etLoginPassword.setText("");
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", "Invalid credentials, Please check :: " + t.getMessage(), "OK", ProviderLogin.this);
            }
        });


    }

    private void login() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Login> call = apiService.login(etLoginMobile.getText().toString(),
                etLoginPassword.getText().toString(),
                "1",
                sessionManager.getString(sessionManager.NOTIFICATION_TOKEN));

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response != null && response.isSuccessful()) {
                    Log.d(TAG, "onResponse: "+ response.body().getStatus());
                    Util.hideProgressDialog(progressDialog);
                    Login login = response.body();
                    if (login.getStatus().equals("200")) {

                        Log.d(TAG, "onResponse: " + "  message " + login.getMessage());
                        Log.d(TAG, "onResponse: " + "  mobile " + login.getData().getMobile());
                        Log.d(TAG, "onResponse: " + "  pin " + "--" + login.getData().getMobilePin());
                        Log.d(TAG, "onResponse: " + " first address " + "--" + login.getData().getAddress().getFirstAddress());
                        Log.d(TAG, "onResponse: " + "contact person " + "--" + login.getData().getContactPerson());
                        Log.d(TAG, "onResponse: " + "phone number" + "--" + login.getData().getPhone());
                        List<Double> list = login.getData().getAddress().getLocation().getCoordinates();
                        Double longitude = list.get(0).doubleValue();
                        Double latitude = list.get(1).doubleValue();
                        Log.d(TAG, "onResponse: " + "longitude" + "--" + longitude);
                        Log.d(TAG, "onResponse: " + "longitude" + "--" + latitude);
                        Log.d(TAG, "onResponse: " + " image " + "--" + login.getData().getProfilePic());

                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, login.getData().getMobile());
                        sessionManager.setString(SessionManager.PROVIDER_PIN, login.getData().getMobilePin());
                        sessionManager.setString(SessionManager.REGISTER_NAME, login.getData().getRegisteredName());
                        sessionManager.setString(SessionManager.EMAIL, login.getData().getEmail());
                        sessionManager.setString(SessionManager.ADDRESS, login.getData().getAddress().getFirstAddress());
                        sessionManager.setString(SessionManager.CONTACT_PERSON,login.getData().getContactPerson());
                        sessionManager.setString(SessionManager.LANDLINE, login.getData().getPhone());
                        sessionManager.setString(SessionManager.LATITUDE, latitude+"");
                        sessionManager.setString(SessionManager.LONGITUDE, longitude+"");
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, login.getData().getProfilePic());
                        sessionManager.setString(SessionManager.SERVICE, service);

                        Intent logout= new Intent(getApplicationContext(), MainActivity.class);
                        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logout);
                        finish();
                    }

                    else {
                        ActionForAll.alertUser("VahanWire", login.getMessage(), "OK", ProviderLogin.this);
                    }

                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after sometime", "OK", ProviderLogin.this);
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.e(TAG, "error::: " + t.getMessage());
                etLoginMobile.setText("");
                etLoginPassword.setText("");
                ActionForAll.alertUser("VahanWire", "Invalid credentials, Please check", "OK", ProviderLogin.this);
            }
        });
    }

    private void loginAmbulance() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<LoginAmbulance> call = apiService.login_ambulance(etLoginMobile.getText().toString(),
                etLoginPassword.getText().toString(),
                sessionManager.getString(SessionManager.DEVICE_ID),
                "1",
                sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));


        call.enqueue(new Callback<LoginAmbulance>() {
            @Override
            public void onResponse(Call<LoginAmbulance> call, Response<LoginAmbulance> response) {
                if (response != null && response.isSuccessful()) {
                    Log.d(TAG, "onResponse: "+ response.body().getStatus());
                    Util.hideProgressDialog(progressDialog);
                    LoginAmbulance login = response.body();
                    if (login.getStatus().equals("200"))
                    {
                        sessionManager.setString(SessionManager.SERVICE, service);
                        Data data = login.getData();
                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, data.getContact());
                        sessionManager.setString(SessionManager.PROVIDER_PIN, data.getMobilePin());
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, data.getOrganisation().getProfilePic());
                        sessionManager.setString(SessionManager.REGISTER_NAME, data.getOrganisation().getOrganisationName());
                        sessionManager.setString(SessionManager.EMAIL, data.getOrganisation().getEmail());
                        sessionManager.setString(SessionManager.PROVIDER_ID, data.getId());
                        Log.e(TAG, "onResponse: image " + sessionManager.getString(SessionManager.PROVIDER_IMAGE));

                        Intent intent= new Intent(getApplicationContext(), AmbulanceProvider.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                        /* Log.d(TAG, "onResponse: " + "  message " + login.getMessage());
                        Log.d(TAG , "onResponse: " + "  mobile " + login.getData().getMobile());
                        Log.d(TAG, "onResponse: " + "  pin " + "--" + login.getData().getMobilePin());
                        Log.d(TAG, "onResponse: " + " first address " + "--" + login.getData().getAddress().getFirstAddress());
                        Log.d(TAG, "onResponse: " + "contact person " + "--" + login.getData().getContactPerson());
                        Log.d(TAG, "onResponse: " + "phone number" + "--" + login.getData().getPhone());
                        List<Double> list = login.getData().getAddress().getLocation().getCoordinates();
                        Double longitude = list.get(0).doubleValue();
                        Double latitude = list.get(1).doubleValue();
                        Log.d(TAG, "onResponse: " + "longitude" + "--" + longitude);
                        Log.d(TAG, "onResponse: " + "longitude" + "--" + latitude);
                        Log.d(TAG, "onResponse: " + " image " + "--" + login.getData().getProfilePic());

                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, login.getData().getMobile());
                        sessionManager.setString(SessionManager.PROVIDER_PIN, login.getData().getMobilePin());
                        sessionManager.setString(SessionManager.REGISTER_NAME, login.getData().getRegisteredName());
                        sessionManager.setString(SessionManager.EMAIL, login.getData().getEmail());
                        sessionManager.setString(SessionManager.ADDRESS, login.getData().getAddress().getFirstAddress());
                        sessionManager.setString(SessionManager.CONTACT_PERSON,login.getData().getContactPerson());
                        sessionManager.setString(SessionManager.LANDLINE, login.getData().getPhone());
                        sessionManager.setString(SessionManager.LATITUDE, latitude+"");
                        sessionManager.setString(SessionManager.LONGITUDE, longitude+"");
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, login.getData().getProfilePic());

                        Intent logout= new Intent(getApplicationContext(), MainActivity.class);
                        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logout);
                        finish();*/
                    }

                    else {
                        Util.hideProgressDialog(progressDialog);
                        ActionForAll.alertUser("VahanWire", login.getMessage(), "OK", ProviderLogin.this);
                    }

                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", ProviderLogin.this);
                }
            }

            @Override
            public void onFailure(Call<LoginAmbulance> call, Throwable t) {
                etLoginMobile.setText("");
                etLoginPassword.setText("");
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", "Invalid credentials, Please check", "OK", ProviderLogin.this);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            ActionForAll.alertChoiseCloseActivity(this);
        }
        return true;
    }

}
