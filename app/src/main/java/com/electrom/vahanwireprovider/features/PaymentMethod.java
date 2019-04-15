package com.electrom.vahanwireprovider.features;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.adapters.PaymentAdapter;
import com.electrom.vahanwireprovider.adapters.PaymentTowAdapter;
import com.electrom.vahanwireprovider.models.payment.Payment;
import com.electrom.vahanwireprovider.models.payment.Payment_array;
import com.electrom.vahanwireprovider.models.payment_tow.PaymentList;
import com.electrom.vahanwireprovider.models.payment_tow.PaymentTow;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.Constant;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethod extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = PaymentMethod.class.getSimpleName();
    RecyclerView recyclerPayment;
    SessionManager sessionManager;
    PaymentAdapter paymentAdapter;
    PaymentTowAdapter paymentAdapter2;
    CustomButton btnSubmitPayment;
    ImageView iv_back_payment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        initView();
    }

    private void initView() {
        sessionManager = SessionManager.getInstance(this);
        recyclerPayment = findViewById(R.id.recyclerPayment);
        btnSubmitPayment = findViewById(R.id.btnSubmitPayment);
        iv_back_payment = findViewById(R.id.iv_back_payment);
        iv_back_payment.setOnClickListener(this);
        recyclerPayment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_PETROL_PUMP))
        {
            getAllPlaymentMethod();
            Log.e(TAG, "initView: " + sessionManager.getString(SessionManager.SERVICE));
        }
        else if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_AMBULANCE))
        {
              ActionForAll.myFlash(getApplicationContext(), "pending");
               Log.e(TAG, "initView: " + sessionManager.getString(SessionManager.SERVICE));
        }
        else if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_MECHNIC_PRO))
        {
            getAllPlaymentMethodMech();
            Log.e(TAG, "initView: " + sessionManager.getString(SessionManager.SERVICE));

        }

        else if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_TOW))
        {
            getAllPlaymentMethodTow();
            Log.e(TAG, "initView: " + sessionManager.getString(SessionManager.SERVICE));
        }
    }

    private void getAllPlaymentMethod()
    {
        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", sessionManager.getString(SessionManager.PROVIDER_MOBILE));

        Call<Payment> call = apiService.getAllPaymentMethod(params);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {

                if(response!=null && response.isSuccessful())
                {
                    Util.hideProgressDialog(progressDialog);
                    Payment payment = response.body();
                    Log.d(TAG, payment.getStatus());

                    List<Payment_array> list = payment.getData();
                    Log.d(TAG, "list size " + list.size()+"");
                    paymentAdapter = new PaymentAdapter(list, PaymentMethod.this, btnSubmitPayment);
                    recyclerPayment.setAdapter(paymentAdapter);
                    paymentAdapter.notifyDataSetChanged();
                }
                else
                {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Low Internet Connection, Please Try after some time", "OK", PaymentMethod.this);
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {

            }
        });
    }

    private void getAllPlaymentMethodMech()
    {
        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", sessionManager.getString(SessionManager.PROVIDER_MOBILE));

        Call<Payment> call = apiService.getAllPaymentMethodMech(params);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {

                if(response!=null && response.isSuccessful())
                {
                    Util.hideProgressDialog(progressDialog);
                    Payment payment = response.body();
                    Log.d(TAG, "list size mech "+ payment.getStatus());

                    List<Payment_array> list = payment.getData();
                    Log.d(TAG, "list size mech " + list.size()+"");
                    paymentAdapter = new PaymentAdapter(list, PaymentMethod.this, btnSubmitPayment);
                    recyclerPayment.setAdapter(paymentAdapter);
                    paymentAdapter.notifyDataSetChanged();
                }
                else
                {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Low Internet Connection, Please Try after some time", "OK", PaymentMethod.this);
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {

            }
        });
    }

    private void getAllPlaymentMethodTow()
    {
        Log.e(TAG, "getAllPlaymentMethodTow: " + "tow payment list" );

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", sessionManager.getString(SessionManager.PROVIDER_MOBILE));

        Call<PaymentTow> call = apiService.getAllPaymentMethodTow(params);
        call.enqueue(new Callback<PaymentTow>() {
            @Override
            public void onResponse(Call<PaymentTow> call, Response<PaymentTow> response) {

                if(response!=null && response.isSuccessful())
                {
                    Util.hideProgressDialog(progressDialog);
                    PaymentTow payment = response.body();
                    Log.d(TAG, "list size mech "+ payment.getStatus());

                    List<PaymentList> data = payment.getData();
                    Log.d(TAG, "list size tow " + data.size()+"");
                    paymentAdapter2 = new PaymentTowAdapter(data, PaymentMethod.this, btnSubmitPayment);
                    recyclerPayment.setAdapter(paymentAdapter2);
                    paymentAdapter2.notifyDataSetChanged();
                }
                else
                {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Low Internet Connection, Please Try after some time", "OK", PaymentMethod.this);
                }
            }

            @Override
            public void onFailure(Call<PaymentTow> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_back_payment:
                finish();
                break;
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
