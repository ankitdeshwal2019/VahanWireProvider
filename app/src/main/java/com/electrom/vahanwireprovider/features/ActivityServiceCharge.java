package com.electrom.vahanwireprovider.features;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.R;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityServiceCharge extends AppCompatActivity {

    private static final String TAG = ActivityServiceCharge.class.getSimpleName();
    SessionManager sessionManager;
    CustomEditText etServiceCharge;
    CustomButton btnServiceCharge;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_charge);
        sessionManager = SessionManager.getInstance(this);
        etServiceCharge = findViewById(R.id.etServiceCharge);
        btnServiceCharge = findViewById(R.id.btnServiceCharge);
        back = findViewById(R.id.back);
        if(sessionManager.getString(SessionManager.SERVICE_CHARGE).length() > 0)
        {
            etServiceCharge.setText(sessionManager.getString(SessionManager.SERVICE_CHARGE));
        }

        btnServiceCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActionForAll.validEditText(etServiceCharge, "service charge", ActivityServiceCharge.this))
                {
                    compaleteRegistrationMechanic();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void compaleteRegistrationMechanic() {
        Log.e(TAG, "service: regstration for mechanic ");
        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.registrationUpdateMechanicNew(
                sessionManager.getString(SessionManager.PROVIDER_MOBILE)
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,""
                ,"",
                "",
                etServiceCharge.getText().toString(),
        "");


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Util.hideProgressDialog(progressDialog);


                if(response!=null && response.isSuccessful())
                {
                    try {
                        String res = response.body().string();
                        Log.e(TAG, "onResponse: " + res);
                        JSONObject object = new JSONObject(res);
                        if(object.getString("status").equals("200"))
                        {
                            new AlertDialog.Builder(ActivityServiceCharge.this)
                                    .setCancelable(false)
                                    .setMessage(object.getString("message"))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent logout = new Intent(getApplicationContext(), MachanicHomePage.class);
                                            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(logout);
                                        }
                                    })
                                    .create().show();
                            Log.e(TAG, "onResponse: " +   object.getString("message"));
                        }
                        else
                        {
                            ActionForAll.alertUserWithCloseActivity("VahanProvider", object.getString("message"), "OK", ActivityServiceCharge.this);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.e(TAG, "onFailure: " + t.getMessage());
                t.printStackTrace();
                ActionForAll.alertUserWithCloseActivity("VahanWire", t.getMessage(), "OK", ActivityServiceCharge.this);
            }
        });
    }
}
