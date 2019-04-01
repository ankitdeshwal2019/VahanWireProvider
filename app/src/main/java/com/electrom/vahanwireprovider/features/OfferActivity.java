package com.electrom.vahanwireprovider.features;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.MainActivity;
import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.models.detail.Offer;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.Constant;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomEditText;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = OfferActivity.class.getSimpleName();
    static CustomButton btnStartDate, btnEndDate;
    static int check_btn = 0;
    SessionManager sessionManager;
    CustomEditText etOfferTitle, etOfferDescription;
    CustomButton btnSubmitOffer;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        initView();
    }

    private void initView() {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPrefs.getString("Offer", null);
        Offer offer = gson.fromJson(json, Offer.class);
        sessionManager = SessionManager.getInstance(this);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        etOfferTitle = findViewById(R.id.etOfferTitle);
        etOfferDescription = findViewById(R.id.etOfferDescription);
        btnSubmitOffer = findViewById(R.id.btnSubmitOffer);
        iv_back = findViewById(R.id.iv_back);
        btnStartDate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        btnSubmitOffer.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        try {
            setData(offer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData(Offer offer) throws Exception {
        etOfferTitle.setText(offer.getTitle());
        etOfferDescription.setText(offer.getDescription());
        btnStartDate.setText(offer.getFrom());
        btnEndDate.setText(offer.getTo());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnStartDate:
                check_btn = 1;
                DialogFragment startDate = new DatePickerFragment();
                startDate.show(getSupportFragmentManager(), "datePicker");
                break;
            case R.id.btnEndDate:
                check_btn = 2;
                DialogFragment endDate = new DatePickerFragment();
                endDate.show(getSupportFragmentManager(), "datePicker");
                break;

            case R.id.btnSubmitOffer:

                isNotEmptyFields();
                break;

            case R.id.iv_back:
                finish();
        }
    }

    private void addOffer() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.offerAdd(sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                etOfferTitle.getText().toString(), etOfferDescription.getText().toString(), btnStartDate.getText().toString(),
                btnEndDate.getText().toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);
                    try {
                        String jsonResponse = response.body().string();
                        Log.d(TAG, jsonResponse);
                        final JSONObject object = new JSONObject(jsonResponse);
                        Log.d(TAG, object.getString("status"));

                        if (object.getString("status").equals("200")) {
                            final String message = object.getString("message");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(OfferActivity.this)
                                            .setTitle("VahanProvider")
                                            .setMessage(message)
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    Intent logout= new Intent(getApplicationContext(), MainActivity.class);
                                                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(logout);
                                                }
                                            }).create().show();
                                   //ActionForAll.alertUserWithCloseActivity("VahanWire", message,"OK", OfferActivity.this);
                                }
                            }, 300);
                        } else {
                            ActionForAll.alertUser("VahanWire", object.getString("message"), "OK", OfferActivity.this);
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
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void addOffermech() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.offerAdd_mech(
                sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                etOfferTitle.getText().toString(), etOfferDescription.getText().toString(),
                btnStartDate.getText().toString(),
                btnEndDate.getText().toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);
                    try {
                        String jsonResponse = response.body().string();
                        Log.d(TAG, jsonResponse);
                        final JSONObject object = new JSONObject(jsonResponse);
                        Log.d(TAG, object.getString("status"));

                        if (object.getString("status").equals("200")) {
                            final String message = object.getString("message");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(OfferActivity.this)
                                            .setTitle("VahanProvider")
                                            .setMessage(message)
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    Intent logout= new Intent(getApplicationContext(), MachanicHomePage.class);
                                                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(logout);
                                                }
                                            }).create().show();
                                    //ActionForAll.alertUserWithCloseActivity("VahanWire", message,"OK", OfferActivity.this);
                                }
                            }, 300);
                        } else {
                            ActionForAll.alertUser("VahanWire", object.getString("message"), "OK", OfferActivity.this);
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
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    private void isNotEmptyFields()
    {
        if(etOfferTitle.getText().toString().trim().length() == 0)
        {
            ActionForAll.alertUser("Tips", "Please enter title", "OK", OfferActivity.this);
        }
        else if(etOfferDescription.getText().toString().trim().length() == 0)
        {
            ActionForAll.alertUser("Tips", "Please enter description.", "OK", OfferActivity.this);
        }
        else if(btnStartDate.getText().toString().contains("Start Date"))
        {
            ActionForAll.alertUser("Tips", "Please enter start offer date.", "OK", OfferActivity.this);
        }

        else if(btnEndDate.getText().toString().contains("End Date"))
        {
            ActionForAll.alertUser("Tips", "Please enter end offer date.", "OK", OfferActivity.this);
        }
        else
        {
            if(sessionManager.getString(SessionManager.SERVICE).contains(Constant.SERVICE_PETROL_PUMP))
            {
                Log.e(TAG, "onClick: " + sessionManager.getString(SessionManager.SERVICE));
                addOffer();
            }
            else if(sessionManager.getString(SessionManager.SERVICE).contains(Constant.SERVICE_AMBULANCE))
            {
                Log.e(TAG, "onClick: " + sessionManager.getString(SessionManager.SERVICE));
            }
            else if(sessionManager.getString(SessionManager.SERVICE).contains(Constant.SERVICE_MECHNIC_PRO))
            {
                Log.e(TAG, "onClick: " + sessionManager.getString(SessionManager.SERVICE));
                addOffermech();
            }

        }

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMinDate(c.getTimeInMillis());
            return  dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String date = year + "-" + setStringMonth(month) + "-" + setStringDay(day);
            if(check_btn == 1)
                btnStartDate.setText(date);
            if(check_btn == 2)
                btnEndDate.setText(date);
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

    private static String setStringMonth(int month){
        if((month + 1) <= 9)
        return "0"+ (month + 1);
        else
        return (month +1)+"";
    }

    private static String setStringDay(int day){
        if(day <= 9)
            return "0"+ day;
        else
            return day + "";
    }

}
