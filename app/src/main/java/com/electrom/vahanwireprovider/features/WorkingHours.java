package com.electrom.vahanwireprovider.features;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.electrom.vahanwireprovider.PetrolPumpHomePage;
import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.location_service_update_track.MyForeGroundService;
import com.electrom.vahanwireprovider.models.mech_status.MechanicStatus;
import com.electrom.vahanwireprovider.models.petrol_status.PetrolStatus;
import com.electrom.vahanwireprovider.models.request_accept.RequestAccept;
import com.electrom.vahanwireprovider.new_app_driver.DriverHomePage;
import com.electrom.vahanwireprovider.new_app_tow.TowHomePage;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.Constant;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomTextView;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkingHours extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = WorkingHours.class.getSimpleName();
    Context context = WorkingHours.this;
    private int mYear, mMonth, mDay, mHour, mMinute;
    RelativeLayout rlStartTime, rlEndTime;
    CustomTextView tvStartTime, tvEndTime,tvPetrolServiceOnOff, tvTagCurrentStatus;
    SessionManager sessionManager;
    CustomButton btnSumbitWorkingHours,btnPetrolServiceOn, btnPetrolServiceOff;
    String START_TIME = "Open Time";
    String END_TIME = "Close Time";
    ImageView iv_back_orking_hours;
    String ACTIVE_STATUS_ON ="ON";
    String ACTIVE_STATUS_OFF ="OFF";
    String loginStatus;
    LinearLayout llStatusContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_hours);
        initView();
    }

    private void initView() {
        sessionManager = SessionManager.getInstance(this);
        rlStartTime = findViewById(R.id.rlStartTime);
        rlEndTime = findViewById(R.id.rlEndTime);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvPetrolServiceOnOff = findViewById(R.id.tvPetrolServiceOnOff);
        btnPetrolServiceOn = findViewById(R.id.btnPetrolServiceOn);
        btnPetrolServiceOff = findViewById(R.id.btnPetrolServiceOff);
        tvTagCurrentStatus = findViewById(R.id.tvTagCurrentStatus);
        llStatusContainer = findViewById(R.id.llStatusContainer);
        //tvTagCurrentStatus.setVisibility(View.INVISIBLE);

        if(sessionManager.getString(SessionManager.WORK_FROM).length() > 0)
        {
            START_TIME = sessionManager.getString(SessionManager.WORK_FROM);
            END_TIME = sessionManager.getString(SessionManager.WORK_TO);
        }

        tvStartTime.setText(START_TIME);
        tvEndTime = findViewById(R.id.tvEndTime);
        iv_back_orking_hours = findViewById(R.id.iv_back_orking_hours);
        tvEndTime.setText(END_TIME);
        btnSumbitWorkingHours = findViewById(R.id.btnSumbitWorkingHours);
        rlEndTime.setOnClickListener(this);
        rlStartTime.setOnClickListener(this);
        btnSumbitWorkingHours.setOnClickListener(this);
        iv_back_orking_hours.setOnClickListener(this);
        btnPetrolServiceOn.setOnClickListener(this);
        btnPetrolServiceOff.setOnClickListener(this);
        loginStatus = sessionManager.getString(SessionManager.ACTIVE_STATUS);

        if(loginStatus.equals("1"))
        {
            btnPetrolServiceOn.setVisibility(View.GONE);
            btnPetrolServiceOff.setVisibility(View.VISIBLE);
            tvPetrolServiceOnOff.setText(ACTIVE_STATUS_ON);
            tvPetrolServiceOnOff.setTextColor(Color.parseColor("#00A600"));
        }
        else
        {
            btnPetrolServiceOn.setVisibility(View.VISIBLE);
            btnPetrolServiceOff.setVisibility(View.GONE);
            tvPetrolServiceOnOff.setText(ACTIVE_STATUS_OFF);
            tvPetrolServiceOnOff.setTextColor(Color.RED);
        }

        if(!sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_PETROL_PUMP))
        {
            tvPetrolServiceOnOff.setVisibility(View.GONE);
            btnPetrolServiceOff.setVisibility(View.GONE);
            btnPetrolServiceOn.setVisibility(View.GONE);
            tvTagCurrentStatus.setVisibility(View.GONE);
            llStatusContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {


        switch (v.getId())
        {
            case R.id.rlStartTime:

                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                Log.e(TAG, "onTimeSet: " + hourOfDay + " : " + minute );
                                START_TIME = setStringHour(hourOfDay) + ":" + setStringMinutes(minute);
                                tvStartTime.setText(START_TIME);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                break;


            case R.id.rlEndTime:
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog1 = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                Log.e(TAG, "onTimeSet: " + hourOfDay + ":" + minute );
                                END_TIME = setStringHour(hourOfDay) + ":" + setStringMinutes(minute);
                                tvEndTime.setText(END_TIME);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog1.show();
                break;

            case R.id.btnSumbitWorkingHours:

                if(!START_TIME.contains("Open Time") && !END_TIME.contains("Close Time"))
                {
                    if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_PETROL_PUMP))
                    {
                        updateTiming();
                    }
                    else if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_MECHNIC_PRO))
                    {
                        updateTimingMech();
                    }

                    else if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_TOW))
                    {
                        updateTimingTow();
                    }
                }
                else
                {
                    ActionForAll.alertUser("VahanProvider", "Please choose open/close timings", "OK", WorkingHours.this);
                }

                break;

            case R.id.iv_back_orking_hours:
                finish();
                break;

            case R.id.btnPetrolServiceOn:
                updateserviceInfo("0");
                break;
            case R.id.btnPetrolServiceOff:
                updateserviceInfo("1");
                break;
        }
        // Get Current Time
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


    private void updateTiming()
    {
        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        Log.e(TAG, "updateTiming: mobile " + sessionManager.getString(SessionManager.PROVIDER_MOBILE));
        Log.e(TAG, "updateTiming: from " + START_TIME);
        Log.e(TAG, "updateTiming: to " + END_TIME);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.petrol_pump_time_update(sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                START_TIME,
                END_TIME);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Util.hideProgressDialog(progressDialog);

                if(response.isSuccessful())
                {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if(object.getString("status").equals("200"))
                        {
                            new AlertDialog.Builder(WorkingHours.this)
                                    .setTitle("VahanProvider")
                                    .setMessage(object.getString("message"))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent logout = new Intent(context, PetrolPumpHomePage.class);
                                            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(logout);

                                        }
                                    }).create().show();
                        }
                        else
                        {
                            ActionForAll.alertUserWithCloseActivity("VahanProvider", object.getString("message"), "OK", WorkingHours.this);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                else
                {
                    ActionForAll.alertUser("VahanWire", "Network busy please try after sometime", "OK", WorkingHours.this);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                t.printStackTrace();
                finish();
            }
        });

    }


    private void updateTimingMech()
    {
        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        Log.e(TAG, "updateTiming: mobile " + sessionManager.getString(SessionManager.PROVIDER_MOBILE));
        Log.e(TAG, "updateTiming: from " + START_TIME);
        Log.e(TAG, "updateTiming: to " + END_TIME);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.mech_time_update(sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                START_TIME,
                END_TIME);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Util.hideProgressDialog(progressDialog);

                if(response.isSuccessful())
                {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if(object.getString("status").equals("200"))
                        {
                            new AlertDialog.Builder(WorkingHours.this)
                                    .setTitle("VahanProvider")
                                    .setMessage(object.getString("message"))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent logout = new Intent(context, MachanicHomePage.class);
                                            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(logout);

                                        }
                                    }).create().show();
                        }
                        else
                        {
                            ActionForAll.alertUserWithCloseActivity("VahanProvider", object.getString("message"), "OK", WorkingHours.this);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                else
                {
                    ActionForAll.alertUser("VahanWire", "Network busy please try after sometime", "OK", WorkingHours.this);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                t.printStackTrace();
                finish();
            }
        });

    }


    private void updateTimingTow()
    {
        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        Log.e(TAG, "updateTimingTow: " + "tow timing update" );
        Log.e(TAG, "updateTiming: mobile " + sessionManager.getString(SessionManager.PROVIDER_MOBILE));
        Log.e(TAG, "updateTiming: from " + START_TIME);
        Log.e(TAG, "updateTiming: to " + END_TIME);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<RequestAccept> call = apiService.tow_time_update(sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                START_TIME,
                END_TIME);

        call.enqueue(new Callback<RequestAccept>() {
            @Override
            public void onResponse(Call<RequestAccept> call, Response<RequestAccept> response) {

                Util.hideProgressDialog(progressDialog);

                if(response.isSuccessful())
                {

                    RequestAccept body = response.body();

                    if(body.getStatus().equals("200"))
                    {

                        new AlertDialog.Builder(WorkingHours.this)
                                .setTitle("VahanProvider")
                                .setMessage(body.getMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                            Intent logout = new Intent(context, TowHomePage.class);
                                            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(logout);

                                    }
                                }).create().show();

                        //ActionForAll.alertUserWithCloseActivity("VahanProvider", body.getMessage(), "OK", WorkingHours.this);
                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanProvider", body.getMessage(), "OK", WorkingHours.this);
                    }

                }

                else
                {
                    ActionForAll.alertUser("VahanWire", "Network busy please try after sometime", "OK", WorkingHours.this);
                }
            }

            @Override
            public void onFailure(Call<RequestAccept> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                t.printStackTrace();
                finish();
            }
        });

    }

    private void updateserviceInfo(final String status_id)
    {
        Log.e(TAG, "mobile " + sessionManager.getString(SessionManager.PROVIDER_MOBILE));
        Log.e(TAG, "status id " +status_id);

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<PetrolStatus> call = apiService.update_status_petrol_pump(sessionManager.getString(SessionManager.PROVIDER_MOBILE), status_id);

        call.enqueue(new Callback<PetrolStatus>() {
            @Override
            public void onResponse(Call<PetrolStatus> call, Response<PetrolStatus> response) {

                Util.hideProgressDialog(progressDialog);

                if(response.isSuccessful())
                {
                    PetrolStatus statusUser = response.body();
                    if(statusUser.getStatus().equals("200"))
                    {
                        Log.d(TAG, "success " + statusUser.getMessage());
                        if(status_id.equals("0"))
                        {
                            tvPetrolServiceOnOff.setText(ACTIVE_STATUS_ON);
                            btnPetrolServiceOn.setVisibility(View.GONE);
                            btnPetrolServiceOff.setVisibility(View.VISIBLE);
                            tvPetrolServiceOnOff.setTextColor(Color.parseColor("#00A600"));

                            sessionManager.setString(SessionManager.ACTIVE_STATUS, "1");

                            /*Intent locationIntent = new Intent(getBaseContext(), MyForeGroundService.class);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                                startForegroundService(locationIntent);
                            } else {
                                //lower then Oreo, just start the service.
                                startService(locationIntent);
                            }*/

                        }
                        else
                        {
                            tvPetrolServiceOnOff.setText(ACTIVE_STATUS_OFF);
                            btnPetrolServiceOn.setVisibility(View.VISIBLE);
                            btnPetrolServiceOff.setVisibility(View.GONE);
                            tvPetrolServiceOnOff.setTextColor(Color.RED);
                            sessionManager.setString(SessionManager.ACTIVE_STATUS, "0");
                        }
                    }
                    else
                    {
                        ActionForAll.alertUser("VahanWire", statusUser.getMessage(), "OK", WorkingHours.this);
                    }
                }
                else
                {
                    ActionForAll.alertUser("VahanWire", "Network busy please try after sometime", "OK", WorkingHours.this);
                }
            }

            @Override
            public void onFailure(Call<PetrolStatus> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", t.getMessage(), "OK", WorkingHours.this);
            }
        });

    }


    private static String setStringHour(int hour){
        if(hour  <= 9)
            return "0"+ hour;
        else
            return hour +"";
    }

    private static String setStringMinutes(int min){
        if(min <= 9)
            return "0"+ min;
        else
            return min + "";
    }

}
