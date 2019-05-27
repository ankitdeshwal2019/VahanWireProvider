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

import com.electrom.vahanwireprovider.PetrolPumpHomePage;
import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.models.detail.Offer;
import com.electrom.vahanwireprovider.models.request_accept.RequestAccept;
import com.electrom.vahanwireprovider.new_app_tow.TowHomePage;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.Constant;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomEditText;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    static String dateStart = "";
    static String dateEnd = "";
    static SimpleDateFormat sformat;

    static int y1 = 0;
    static int m1 = 0;
    static int d1 = 0;

    static int y2 = 0;
    static int m2 = 0;
    static int d2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        initView();
    }

    private void initView() {

        sformat = new SimpleDateFormat("yyyy-MM-dd");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();

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

        if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_TOW))
        {
            String json = sharedPrefs.getString("Offer_tow", "");
            Log.e(TAG, "initView: " + json);


            try {
                JSONArray array = new JSONArray(json);
                for(int i = 0 ; i < array.length(); i++)
                {
                    JSONObject value = array.getJSONObject(i);
                    etOfferTitle.setText(value.getString("title"));
                    etOfferDescription.setText(value.getString("description"));
                    btnStartDate.setText(value.getString("from"));
                    btnEndDate.setText(value.getString("to"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else
        {
            String json = sharedPrefs.getString("Offer", null);
            Offer offer = gson.fromJson(json, Offer.class);
            try {
                setData(offer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setData(Offer offer) {
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
                if(y2 >= y1 && m2 >= m1 && d2 >=d1 )
                {
                    isNotEmptyFields();
                }
                else
                {
                    ActionForAll.myFlash(getApplicationContext(), "Please choose valid date");
                }

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
                                                    Intent logout= new Intent(getApplicationContext(), PetrolPumpHomePage.class);
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

    private void addOfferTow() {

        Log.e(TAG, "addOfferTow: " + "tow offer" );

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<RequestAccept> call = apiService.offerAddTow(
                sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                etOfferTitle.getText().toString(), etOfferDescription.getText().toString(),
                btnStartDate.getText().toString(),
                btnEndDate.getText().toString());

        call.enqueue(new Callback<RequestAccept>() {
            @Override
            public void onResponse(Call<RequestAccept> call, Response<RequestAccept> response) {
                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);

                    final RequestAccept body = response.body();

                    if(body.getStatus().equals("200"))
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(OfferActivity.this)
                                        .setTitle("VahanProvider")
                                        .setMessage(body.getMessage())
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent logout= new Intent(getApplicationContext(), TowHomePage.class);
                                                logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(logout);
                                            }
                                        }).create().show();
                                //ActionForAll.alertUserWithCloseActivity("VahanWire", message,"OK", OfferActivity.this);
                            }
                        }, 300);
                    } else {
                        ActionForAll.alertUser("VahanWire", body.getMessage(), "OK", OfferActivity.this);
                    }
                }
                else
                {
                    ActionForAll.alertUser("VahanWire", "No connection please try after sometime", "OK", OfferActivity.this);
                }
            }

            @Override
            public void onFailure(Call<RequestAccept> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void isNotEmptyFields() {
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

            else if(sessionManager.getString(SessionManager.SERVICE).contains(Constant.SERVICE_TOW))
            {
                Log.e(TAG, "onClick: " + sessionManager.getString(SessionManager.SERVICE));
                addOfferTow();
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

            if(check_btn == 1)
            {

                y1 = year;
                m1 = month;
                d1 = day;
                dateStart = y1 + "-" + setStringMonth(m1) + "-" + setStringDay(d1);
                btnStartDate.setText(dateStart);
                Log.e(TAG, "onDateSet: " + dateStart );
            }

            if(check_btn == 2)
            {
                y2 = year;
                m2= month;
                d2 = day;

                dateEnd = y2 + "-" + setStringMonth(m2) + "-" + setStringDay(d2);
                btnEndDate.setText(dateEnd);
                Log.e(TAG, "onDateSet: " + dateStart );
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
