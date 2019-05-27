package com.electrom.vahanwireprovider.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.features.FacilitynPaymentMethod;
import com.electrom.vahanwireprovider.features.PaymentMethod;
import com.electrom.vahanwireprovider.models.services.Datum;
import com.electrom.vahanwireprovider.models.services.Service;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.Constant;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.NoInternet;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    private static final String TAG = ServiceAdapter.class.getSimpleName();
    SessionManager sessionManager;
    NoInternet diologInternet;

    public static Context context;
    LayoutInflater layoutinflater;
    private List<Datum> list;
    private CustomButton submit;
    ProgressDialog pd;

    int previousPosition = 0;
    DecimalFormat decimalFormat;

    public ServiceAdapter(List<Datum> list, Context context, CustomButton submit) {
        this.context = context;
        this.list = list;
        this.submit = submit;
        sessionManager = SessionManager.getInstance(context);
        pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        layoutinflater = LayoutInflater.from(context);
    }

    @Override
    public ServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        decimalFormat = new DecimalFormat("0.00");
        final View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        ServiceAdapter.ViewHolder viewHolder = new ServiceAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ServiceAdapter.ViewHolder holder, final int position) {

        holder.tvServiceName.setText(list.get(position).getService());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(list.get(position).isSelected());
        holder.checkBox.setTag(list.get(position));

        if(list.get(position).getCheckedStatus() == 1)
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);

        final Datum service = list.get(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    service.setCheckedStatus(1);
                }
                else
                {
                    service.setCheckedStatus(0);
                }
                list.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //ActionForAll.myFlash(mContext, "click " + position);
                StringBuilder builder = new StringBuilder();
                String ids;

                for(Datum service : list)
                {
                                 /* Log.d(TAG, "item checked value :: " +service.getChecked());
                                    Log.d(TAG, "item labal :: " +service.getLabel());
                                    Log.d(TAG, "item ids :: " +service.getId());*/

                    if(service.getCheckedStatus()==1)
                    {
                        builder.append(service.getId() +",");
                    }
                }
                ids = finalIds(builder.toString());
                if(ids != null)
                {
                    if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_PETROL_PUMP))
                    {
                        Log.e(TAG, "adapter: " + sessionManager.getString(SessionManager.SERVICE) );

                        if(ids.length() > 0)
                        {
                            updateserviceInfo(ids);
                        }
                        else
                        {
                            ActionForAll.alertUser("", "Please select at least one facility", "OK", (Activity)context);
                        }

                    }
                    else if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_AMBULANCE))
                    {
                        Log.e(TAG, "adapter " + sessionManager.getString(SessionManager.SERVICE) );
                        ActionForAll.myFlash(context, "API not set");
                    }
                    else if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_MECHNIC_PRO))
                    {
                        Log.e(TAG, "adapter: " + sessionManager.getString(SessionManager.SERVICE) );
                        if(ids.length() > 0)
                        {
                            updateserviceInfoMech(ids);
                        }
                        else
                        {
                            ActionForAll.alertUser("", "Please select at least one facility", "OK", (Activity)context);
                        }

                    }

                }

                Log.d(TAG, "final ids :: " +ids);


                /*new AlertDialog.Builder(mContext)
                        .setTitle("Update Services")
                        .setMessage("Are you sure want to update service?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //ActionForAll.myFlash(mContext, "click " + position);
                                StringBuilder builder = new StringBuilder();
                                String ids;

                                for(CityData service : list)
                                {
                                 *//* Log.d(TAG, "item checked value :: " +service.getChecked());
                                    Log.d(TAG, "item labal :: " +service.getLabel());
                                    Log.d(TAG, "item ids :: " +service.getId());*//*

                                    if(service.getCheckedStatus()==1)
                                    {
                                        builder.append(service.getId() +",");
                                    }
                                }
                                ids = finalIds(builder.toString());
                                if(ids != null)
                                {
                                    //updateserviceInfo(ids);
                                }

                                Log.d(TAG, "final ids :: " +ids);
                                dialog.dismiss();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        })
                        .create().show();*/
            }
        });

    }

    // Filter Class
    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder

    {

        public TextView tvServiceName;
        public CheckBox checkBox;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            tvServiceName = itemLayoutView.findViewById(R.id.tvItemServiceName);
            checkBox = itemLayoutView.findViewById(R.id.checkBoxService);
        }
        //  mapInterface.mapIsReadyToSet(this,position);
    }

    private void updateserviceInfo(String service_ids){

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.service_update(sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                service_ids);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);
                    try {

                        String jsonResponse = response.body().string();
                        Log.d(TAG, jsonResponse);
                        JSONObject object = new JSONObject(jsonResponse);
                        Log.d(TAG, object.getString("status"));

                        if(object.getString("status").equals("200"))
                        {
                            ActionForAll.alertUserWithCloseActivity("VahanWire", object.getString("message"), "OK", (FacilitynPaymentMethod)context);
                        }
                        else
                        {
                            ActionForAll.alertUser("VahanWire", object.getString("message"), "OK", (FacilitynPaymentMethod)context);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", (FacilitynPaymentMethod)context);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void updateserviceInfoMech(String service_ids){

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.service_update_mech(sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                service_ids);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);
                    try {

                        String jsonResponse = response.body().string();
                        Log.d(TAG, jsonResponse);
                        JSONObject object = new JSONObject(jsonResponse);
                        Log.d(TAG, object.getString("status"));

                        if(object.getString("status").equals("200"))
                        {
                            ActionForAll.alertUserWithCloseActivity("VahanWire", object.getString("message"), "OK", (FacilitynPaymentMethod)context);
                        }
                        else
                        {
                            ActionForAll.alertUser("VahanWire", object.getString("message"), "OK", (FacilitynPaymentMethod)context);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", (FacilitynPaymentMethod)context);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private static String finalIds(String str) {
        if(str!=null && str.length() > 1)
            return str.substring(0, str.length() - 1);
        else
            return "";
    }
}

