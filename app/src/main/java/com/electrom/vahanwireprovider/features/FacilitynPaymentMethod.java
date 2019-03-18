package com.electrom.vahanwireprovider.features;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.adapters.PaymentAdapter;
import com.electrom.vahanwireprovider.adapters.ServiceAdapter;
import com.electrom.vahanwireprovider.models.payment.Payment;
import com.electrom.vahanwireprovider.models.services.Datum;
import com.electrom.vahanwireprovider.models.payment.Payment_array;
import com.electrom.vahanwireprovider.models.services.Service;
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

public class FacilitynPaymentMethod extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = FacilitynPaymentMethod.class.getSimpleName();
    RecyclerView recyclerFacility;
    CustomButton btnSubmitPaymentnFacility;
    ServiceAdapter serviceAdapter;
    SessionManager sessionManager;
    ImageView iv_back_facility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilityn_payment_method);
        initView();
    }

    private void initView() {
        sessionManager = SessionManager.getInstance(this);
        recyclerFacility = findViewById(R.id.recyclerFecility);
        iv_back_facility = findViewById(R.id.iv_back_facility);
        iv_back_facility.setOnClickListener(this);

        btnSubmitPaymentnFacility = findViewById(R.id.btnSubmitFacility);

        recyclerFacility.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_PETROL_PUMP))
        {
            Log.e(TAG, "initView: " + sessionManager.getString(SessionManager.SERVICE) );
            getAllServices();
        }
        else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_AMBULANCE))
        {
            Log.e(TAG, "initView: " + sessionManager.getString(SessionManager.SERVICE) );
        }
        else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_MECHNIC_PRO))
        {
            Log.e(TAG, "initView: " + sessionManager.getString(SessionManager.SERVICE) );
            getAllServicesMechanic();
        }

        /*paymentAdapter = new PaymentAdapter(list, this, btnSubmitPaymentnFacility);
        serviceAdapter = new ServiceAdapter(list, this, btnSubmitPaymentnFacility);
        recyclerPayment.setAdapter(paymentAdapter);
        recyclerFacility.setAdapter(serviceAdapter);
        paymentAdapter.notifyDataSetChanged();
        serviceAdapter.notifyDataSetChanged();*/
    }


    private void getAllServices() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", sessionManager.getString(SessionManager.PROVIDER_MOBILE));

        Call<Service> call = apiService.getAllServices(params);

        call.enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {

                if(response!=null && response.isSuccessful())
                {
                    Util.hideProgressDialog(progressDialog);
                    Service service = response.body();
                    Log.d(TAG, service.getStatus());

                    List<Datum> list = service.getData();
                    Log.d(TAG, "list size " + list.size()+"");
                    serviceAdapter = new ServiceAdapter(list, FacilitynPaymentMethod.this, btnSubmitPaymentnFacility);
                    recyclerFacility.setAdapter(serviceAdapter);
                    serviceAdapter.notifyDataSetChanged();
                }
                else
                {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Low Internet Connection, Please Try after some time", "OK", FacilitynPaymentMethod.this);
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUserWithCloseActivity("VahanWire", "Low Internet Connection, Please Try after some time", "OK", FacilitynPaymentMethod.this);
            }
        });
    }

    private void getAllServicesMechanic() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", sessionManager.getString(SessionManager.PROVIDER_MOBILE));

        Call<Service> call = apiService.getAllServicesMechanic(params);

        call.enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {

                if(response!=null && response.isSuccessful())
                {
                    Util.hideProgressDialog(progressDialog);
                    Service service = response.body();
                    Log.d(TAG, service.getStatus());

                    List<Datum> list = service.getData();
                    Log.d(TAG, "list size " + list.size()+"");
                    serviceAdapter = new ServiceAdapter(list, FacilitynPaymentMethod.this, btnSubmitPaymentnFacility);
                    recyclerFacility.setAdapter(serviceAdapter);
                    serviceAdapter.notifyDataSetChanged();
                }
                else
                {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Low Internet Connection, Please Try after some time", "OK", FacilitynPaymentMethod.this);
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUserWithCloseActivity("VahanWire", "Low Internet Connection, Please Try after some time", "OK", FacilitynPaymentMethod.this);
            }
        });
    }


    @Override
    public void onClick(View v) {
       switch (v.getId())
       {
           case R.id.iv_back_facility:
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
