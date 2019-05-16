package com.electrom.vahanwireprovider.features;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.adapters.PaymentAdapter;
import com.electrom.vahanwireprovider.adapters.ServiceAdapter;
import com.electrom.vahanwireprovider.models.payment.Payment;
import com.electrom.vahanwireprovider.models.req.Req;
import com.electrom.vahanwireprovider.models.services.Datum;
import com.electrom.vahanwireprovider.models.payment.Payment_array;
import com.electrom.vahanwireprovider.models.services.Service;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.Constant;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomEditText;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacilitynPaymentMethod extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = PreServices.class.getSimpleName();
    RecyclerView recyclerFacility;
    CustomButton btnSubmitPaymentnFacility;
    ServiceAdapter serviceAdapter;
    SessionManager sessionManager;
    ImageView iv_back_facility;
    CustomEditText etAddService;
    CustomButton btnAddService;

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
        btnAddService = findViewById(R.id.btnAddService);
        etAddService = findViewById(R.id.etAddService);
        btnAddService.setOnClickListener(this);
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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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

      private void mech_service_add() {
        Log.e(TAG, "main provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));
        Log.e(TAG, "provider text  " + etAddService.getText().toString());

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Req> call = apiService.mech_service_add(
                sessionManager.getString(SessionManager.PROVIDER_ID),
                etAddService.getText().toString());

        call.enqueue(new Callback<Req>() {
            @Override
            public void onResponse(Call<Req> call, Response<Req> response) {

                Util.hideProgressDialog(progressDialog);

                if (response.isSuccessful()) {
                    Req request = response.body();

                    if (request.getStatus().equals("200")) {

                        getAllServicesMechanic();
                        //ActionForAll.alertUserWithCloseActivity("VahanWire",request.getMessage(),"OK", FacilitynPaymentMethod.this);
                    } else {
                        ActionForAll.alertUserWithCloseActivity("VahanProvider", request.getMessage(), "OK", FacilitynPaymentMethod.this);
                    }
                } else {
                    ActionForAll.alertUserWithCloseActivity("VahanProvider", "Network busy please try after sometime", "OK", FacilitynPaymentMethod.this);
                }
            }

            @Override
            public void onFailure(Call<Req> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanProvider", t.getMessage(), "OK", FacilitynPaymentMethod.this);
            }
        });
    }

    private void petrol_pump_service_add() {
        Log.e(TAG, "main provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));
        Log.e(TAG, "provider text  " + etAddService.getText().toString());

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Req> call = apiService.petrol_pump_service_add(
                sessionManager.getString(SessionManager.PROVIDER_ID),
                etAddService.getText().toString());

        call.enqueue(new Callback<Req>() {
            @Override
            public void onResponse(Call<Req> call, Response<Req> response) {

                Util.hideProgressDialog(progressDialog);

                if (response.isSuccessful()) {
                    Req request = response.body();

                    if (request.getStatus().equals("200")) {

                        getAllServices();
                        //ActionForAll.alertUserWithCloseActivity("VahanWire",request.getMessage(),"OK", FacilitynPaymentMethod.this);
                    } else {
                        ActionForAll.alertUserWithCloseActivity("VahanProvider", request.getMessage(), "OK", FacilitynPaymentMethod.this);
                    }
                } else {
                    ActionForAll.alertUserWithCloseActivity("VahanProvider", "Network busy please try after sometime", "OK", FacilitynPaymentMethod.this);
                }
            }

            @Override
            public void onFailure(Call<Req> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanProvider", t.getMessage(), "OK", FacilitynPaymentMethod.this);
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

           case R.id.btnAddService:

               if(etAddService.getText().toString().trim().length() > 5)
               {

                   if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_PETROL_PUMP))
                   {
                       Log.e(TAG, "initView: " + sessionManager.getString(SessionManager.SERVICE) );
                       petrol_pump_service_add();
                   }

                   else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_MECHNIC_PRO))
                   {
                       Log.e(TAG, "initView: " + sessionManager.getString(SessionManager.SERVICE) );
                       mech_service_add();
                   }
                   else
                   {
                       ActionForAll.myFlash(getApplicationContext(), "No Service type found");
                   }
               }
               else
               {
                   ActionForAll.alertUser("Vahanprovider", "Service must be more then 5 characters.", "OK", FacilitynPaymentMethod.this);
               }
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
