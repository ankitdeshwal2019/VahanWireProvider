package com.electrom.vahanwireprovider.ragistration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.electrom.vahanwireprovider.MainActivity;
import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.models.detail.Detail;
import com.electrom.vahanwireprovider.models.login.Login;
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
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (ActionForAll.validMobileEditText(etLoginMobile, "mobile number", ProviderLogin.this) &&
                        ActionForAll.validEditText(etLoginPassword, "valid pin", ProviderLogin.this)) {
                    login();
                }
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;

            case R.id.btnRegister:
                startActivity(new Intent(getApplicationContext(), RegisterMobile.class));
                break;

        }
    }

    private void login() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Login> call = apiService.login(etLoginMobile.getText().toString(), etLoginPassword.getText().toString());

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
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", ProviderLogin.this);
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.e(TAG, "error::: " + t.getMessage());
            }
        });





       /* call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {

                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);
                    Detail login = response.body();
                    if (login.getStatus().equals("200")) {
                        Log.d(TAG, "login " + login.getMessage());

                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, login.getData().getMobile());
                        sessionManager.setString(SessionManager.PROVIDER_PIN, login.getData().getAccess().getMobilePin());
                        sessionManager.setString(SessionManager.REGISTER_NAME, login.getData().getRegisteredName());
                        sessionManager.setString(SessionManager.EMAIL, login.getData().getEmail());
                        Log.d(TAG, "onResponse: " + login.getData().getMobile() + "--" + login.getData().getAccess().getMobilePin());
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        ActionForAll.alertUser("VahanWire", login.getMessage(), "OK", ProviderLogin.this);
                    }

                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", ProviderLogin.this);
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });*/

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
