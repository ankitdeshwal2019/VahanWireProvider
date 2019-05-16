package com.electrom.vahanwireprovider.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.PetrolPumpHomePage;
import com.electrom.vahanwireprovider.features.ActivityServiceCharge;
import com.electrom.vahanwireprovider.features.AddnExpert;
import com.electrom.vahanwireprovider.features.AmbulanceHomePage;
import com.electrom.vahanwireprovider.features.BookingHistory;
import com.electrom.vahanwireprovider.features.BookingHistoryMechanic;
import com.electrom.vahanwireprovider.features.PreServices;
import com.electrom.vahanwireprovider.features.SelectionActivity;
import com.electrom.vahanwireprovider.new_app_driver.BookingHistoryDriver;
import com.electrom.vahanwireprovider.features.FacilitynPaymentMethod;
import com.electrom.vahanwireprovider.features.MachanicHomePage;
import com.electrom.vahanwireprovider.features.MechanicProfile;
import com.electrom.vahanwireprovider.features.OfferActivity;
import com.electrom.vahanwireprovider.features.PaymentMethod;
import com.electrom.vahanwireprovider.features.ProfileActivity;
import com.electrom.vahanwireprovider.features.SelectBrand;
import com.electrom.vahanwireprovider.features.SelectIssue;
import com.electrom.vahanwireprovider.features.UploadSocial;
import com.electrom.vahanwireprovider.features.WorkingHours;
import com.electrom.vahanwireprovider.models.logout.Logout;
import com.electrom.vahanwireprovider.new_app_driver.DriverHomePage;
import com.electrom.vahanwireprovider.new_app_tow.ProfileUpdateTow;
import com.electrom.vahanwireprovider.new_app_tow.TowHomePage;
import com.electrom.vahanwireprovider.ragistration.BeforeLogin;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itrs on 11/22/2016.
 */

public class CodeMinimisations {

    private static final String TAG = CodeMinimisations.class.getSimpleName();
    private static FragmentActivity myContext;
    static SessionManager sessionManager;

    public CodeMinimisations(FragmentActivity myContext) {
        this.myContext = myContext;

    }

    public static void largeSwitchCase(Context context, String menuItem) {
        sessionManager = SessionManager.getInstance(context);

        Intent in = null;

        switch (menuItem) {

            case "Booking History":

                if(sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_MECHNIC_PRO))
                {
                    Log.e(TAG, "largeSwitchCase: " + "mechanic" );
                    in = new Intent(context, BookingHistoryMechanic.class);
                    context.startActivity(in);
                }

                else
                {
                    Log.e(TAG, "largeSwitchCase: " + "simple" );
                    in = new Intent(context, BookingHistory.class);
                    context.startActivity(in);
                }

                break;

            case "Assigned Task":

                if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_DRIVER))
                {
                    Log.e(TAG, "largeSwitchCase: " + "driver" );
                    in = new Intent(context, BookingHistoryDriver.class);
                    context.startActivity(in);
                }
                break;

            case "Home":
                if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_PETROL_PUMP)) {
                    in = new Intent(context, PetrolPumpHomePage.class);
                    context.startActivity(in);
                    ((Activity) context).finish();

                } else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_AMBULANCE)) {
                    in = new Intent(context, AmbulanceHomePage.class);
                    context.startActivity(in);
                    ((Activity) context).finish();
                } else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_MECHNIC_PRO)) {
                    in = new Intent(context, MachanicHomePage.class);
                    context.startActivity(in);
                    ((Activity) context).finish();
                }
                else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_TOW)) {
                    in = new Intent(context, TowHomePage.class);
                    context.startActivity(in);
                    ((Activity) context).finish();
                }
                else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_DRIVER)){
                    in = new Intent(context, DriverHomePage.class);
                    context.startActivity(in);
                    ((Activity) context).finish();
                }

                break;

            case "Profile":

               /* in = new Intent(context, ProfileActivity.class);
                context.startActivity(in);*/

                if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_PETROL_PUMP)) {
                    in = new Intent(context, ProfileActivity.class);
                    context.startActivity(in);

                } else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_AMBULANCE)) {
                    in = new Intent(context, ProfileActivity.class);
                    context.startActivity(in);
                }

                else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_MECHNIC_PRO)) {
                    in = new Intent(context, MechanicProfile.class);
                    context.startActivity(in);
                }

                else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_TOW)) {
                        in = new Intent(context, ProfileUpdateTow.class);
                        context.startActivity(in);
                }
                break;

            case "My Introduction":
                if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_MECHNIC_PRO)) {
                    in = new Intent(context, UploadSocial.class);
                    context.startActivity(in);
                }
                break;

            case "Offers":

                in = new Intent(context, OfferActivity.class);
                context.startActivity(in);
                break;


            case "Visiting Charges":
                // ActionForAll.myFlash(context, "Offer page");
                in = new Intent(context, ActivityServiceCharge.class);
                context.startActivity(in);
                break;

                case "Write as Expert":
                in = new Intent(context, AddnExpert.class);
                context.startActivity(in);
                break;

            case "Time and Schedule":
                // ActionForAll.myFlash(context, "Time and Schedule page");
                in = new Intent(context, WorkingHours.class);
                context.startActivity(in);
                break;

            case "Facilities":
                // ActionForAll.myFlash(context, "Facility and Payment method page");
                in = new Intent(context, FacilitynPaymentMethod.class);
                context.startActivity(in);
                break;

            case "Payments":
                // ActionForAll.myFlash(context, "Facility and Payment method page");
                in = new Intent(context, PaymentMethod.class);
                context.startActivity(in);
                break;

            case "Select brands":
                // ActionForAll.myFlash(context, "Facility and Payment method page");
                in = new Intent(context, SelectBrand.class);
                context.startActivity(in);
                break;

            case "Select Emergency Services":
                // ActionForAll.myFlash(context, "Facility and Payment method page");
                in = new Intent(context, SelectIssue.class);
                context.startActivity(in);
                break;

                case "Select Pre-Request Services":
                // ActionForAll.myFlash(context, "Facility and Payment method page");
                in = new Intent(context, PreServices.class);
                context.startActivity(in);
                break;

            case "About Us":
                Intent about = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.vahanwire.com/provider-about-us"));
                context.startActivity(about);
                break;

            case "Terms of use":
                Intent terms = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.vahanwire.com/provider-terms-and-conditions"));
                context.startActivity(terms);
                break;

            case "Privacy Policy":
                Intent policy = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.vahanwire.com/provider-privacy-policy"));
                context.startActivity(policy);
                break;

            case "Contact Us":
                Intent contact = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.vahanwire.com/contact-us"));
                context.startActivity(contact);
                break;

            case "Log out":
                logoutAlert(context);
                break;
        }
    }

    private static void logoutAlert(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle("LogOut")
                .setMessage("Do you want to logout?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_AMBULANCE)) {
                            logoutMethodAmbulance();
                        } else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_MECHNIC_PRO)) {
                            logoutMethodMechanic();
                        }
                        else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_DRIVER)) {
                            logoutMethodDriver();
                        }
                        else if (sessionManager.getString(SessionManager.SERVICE).equals(Constant.SERVICE_TOW)) {
                            logoutMethodTow();
                        }

                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, "");
                        sessionManager.setString(SessionManager.PROVIDER_PIN, "");
                        sessionManager.setString(SessionManager.REGISTER_NAME, "");
                        sessionManager.setString(SessionManager.EMAIL, "");
                        sessionManager.setString(SessionManager.ADDRESS, "");
                        sessionManager.setString(SessionManager.CONTACT_PERSON, "");
                        sessionManager.setString(SessionManager.LANDLINE, "");
                        sessionManager.setString(SessionManager.LATITUDE, "");
                        sessionManager.setString(SessionManager.LONGITUDE, "");
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, "");
                        sessionManager.setString(SessionManager.LOGIN_STATUS, "");
                        sessionManager.setString(SessionManager.SERVICE, "");
                        sessionManager.setString(SessionManager.DEVICE_ID, "");
                        sessionManager.setString(SessionManager.NOTIFICATION_TOKEN, "");
                        sessionManager.setString(SessionManager.ACTIVE_STATUS, "");
                        sessionManager.setString(SessionManager.PROVIDER_ID, "");
                        sessionManager.setString(SessionManager.COUNRTY, "");
                        sessionManager.setString(SessionManager.STATE, "");
                        sessionManager.setString(SessionManager.CITY, "");
                        sessionManager.setString(SessionManager.PINCODE, "");
                        sessionManager.setString(SessionManager.CITY_TAG, "");
                        sessionManager.setString(SessionManager.STATE_TAG, "");
                        sessionManager.setString(SessionManager.COUNRTY_TAG, "");
                        sessionManager.setString(SessionManager.BOOKING_ID, "");
                        sessionManager.setString(SessionManager.BOOKING_STATUS, "");
                        sessionManager.setString(SessionManager.BOOKING_STATUS_USER, "");
                        sessionManager.setString(SessionManager.MAIN_PROVIDER, "");
                        sessionManager.setString(SessionManager.NOTI_NAME, "");
                        sessionManager.setString(SessionManager.NOTI_ISSUE, "");
                        sessionManager.setString(SessionManager.PROVIDER_VEHICLE, "");
                        sessionManager.setString(SessionManager.WORK_FROM, "");
                        sessionManager.setString(SessionManager.WORK_TO, "");
                        sessionManager.setString(SessionManager.PRO_CERTIFICTION_NAME, "");
                        sessionManager.setString(SessionManager.PRO_DOB, "");
                        sessionManager.setString(SessionManager.PRO_HIGH_QULALIFICATION, "");
                        sessionManager.setString(SessionManager.PRO_MARRITAL_STATUS, "");
                        sessionManager.setString(SessionManager.PRO_TOTAL_EXP, "");
                        sessionManager.setString(SessionManager.PRO_WEB, "");
                        sessionManager.setString(SessionManager.PRO_GST, "");
                        sessionManager.setString(SessionManager.PRO_FACEBOOK, "");
                        sessionManager.setString(SessionManager.PRO_TWEET, "");
                        sessionManager.setString(SessionManager.PRO_INSTA, "");
                        sessionManager.setString(SessionManager.VEHCILE_NUMBER, "");
                        sessionManager.setString(SessionManager.SERVICE_CHARGE, "");
                        sessionManager.setString(SessionManager.QUOTE_URL, "");
                        sessionManager.setString(SessionManager.VEHCILE_TYPE, "");
                        sessionManager.setString(SessionManager.MINI_QUOTE_URL, "");
                        sessionManager.setString(SessionManager.PRO_PERSONAL_PAN, "");
                        sessionManager.setString(SessionManager.PRO_SPECIAL_TALENT, "");
                        sessionManager.setString(SessionManager.PRO_ORG_PAN, "");
                        sessionManager.setString(SessionManager.PRO_ORG_ID, "");
                        sessionManager.setString(SessionManager.NAV_NAME_MECH, "");

                        dialog.dismiss();
                        Intent logout = new Intent(context, BeforeLogin.class);
                        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(logout);

                        ((Activity) context).finish();


                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                }).create().show();
    }

    public static void navListener(ImageView nav, final NavigationView mNavigationView, final DrawerLayout mDrawerLayout, final Context context) {

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public static void navigationItemListener(final NavigationView mNavigationView, final Context context) {
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                CodeMinimisations.largeSwitchCase(context, menuItem.getTitle().toString());
                return true;
            }
        });
    }

    private static void logoutMethodAmbulance() {
        Log.e(TAG, "logoutMethodAmbulance: " + "ambulance" );
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Logout> call = apiService.logoutAmbulance(sessionManager.getString(SessionManager.PROVIDER_ID));

        call.enqueue(new Callback<Logout>() {
            @Override
            public void onResponse(Call<Logout> call, Response<Logout> response) {


                if (response.isSuccessful()) {
                    Logout bookingStatus = response.body();

                    if (bookingStatus.getStatus().equals("200")) {
                        Log.d(TAG, "success " + bookingStatus.getMessage());

                        //ActionForAll.alertUserWithCloseActivity("VahanWire","Request cancelled !","OK", BookingStatusMechanic.this);
                    } else {
                        Log.e(TAG, " else  " + bookingStatus.getMessage());
                        //ActionForAll.alertUserWithCloseActivity("VahanWire", bookingStatus.getMessage(), "OK", BookingStatusMechanic.this);
                    }
                } else {
                    Log.e(TAG, " else  " + "Network busy please try after sometime");
                    //ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", MyForeGroundService.this);
                }
            }

            @Override
            public void onFailure(Call<Logout> call, Throwable t) {

                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    private static void logoutMethodMechanic() {

        Log.e(TAG, "logoutMethodAmbulance: " + "mechanic" );

        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Logout> call = apiService.logoutMechanic(sessionManager.getString(SessionManager.PROVIDER_ID));

        call.enqueue(new Callback<Logout>() {
            @Override
            public void onResponse(Call<Logout> call, Response<Logout> response) {


                if (response.isSuccessful()) {
                    Logout bookingStatus = response.body();

                    if (bookingStatus.getStatus().equals("200")) {
                        Log.d(TAG, "success " + bookingStatus.getMessage());


                        //ActionForAll.alertUserWithCloseActivity("VahanWire","Request cancelled !","OK", BookingStatusMechanic.this);
                    } else {
                        Log.e(TAG, " else  " + bookingStatus.getMessage());
                        //ActionForAll.alertUserWithCloseActivity("VahanWire", bookingStatus.getMessage(), "OK", BookingStatusMechanic.this);
                    }
                } else {
                    Log.e(TAG, " else  " + "Network busy please try after sometime");
                    //ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", MyForeGroundService.this);
                }
            }

            @Override
            public void onFailure(Call<Logout> call, Throwable t) {

                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    private static void logoutMethodDriver() {

        Log.e(TAG, "logoutMethodAmbulance: " + "driver" );

        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Logout> call = apiService.logoutDriver(sessionManager.getString(SessionManager.PROVIDER_ID));

        call.enqueue(new Callback<Logout>() {
            @Override
            public void onResponse(Call<Logout> call, Response<Logout> response) {


                if (response.isSuccessful()) {
                    Logout bookingStatus = response.body();

                    if (bookingStatus.getStatus().equals("200")) {
                        Log.d(TAG, "success " + bookingStatus.getMessage());


                        //ActionForAll.alertUserWithCloseActivity("VahanWire","Request cancelled !","OK", BookingStatusMechanic.this);
                    } else {
                        Log.e(TAG, " else  " + bookingStatus.getMessage());
                        //ActionForAll.alertUserWithCloseActivity("VahanWire", bookingStatus.getMessage(), "OK", BookingStatusMechanic.this);
                    }
                } else {
                    Log.e(TAG, " else  " + "Network busy please try after sometime");
                    //ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", MyForeGroundService.this);
                }
            }

            @Override
            public void onFailure(Call<Logout> call, Throwable t) {

                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    private static void logoutMethodTow() {

        Log.e(TAG, "logoutMethoe: " + "tow logout" );

        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Logout> call = apiService.logoutTow(sessionManager.getString(SessionManager.PROVIDER_ID));

        call.enqueue(new Callback<Logout>() {
            @Override
            public void onResponse(Call<Logout> call, Response<Logout> response) {


                if (response.isSuccessful()) {
                    Logout bookingStatus = response.body();

                    if (bookingStatus.getStatus().equals("200")) {
                        Log.d(TAG, "success " + bookingStatus.getMessage());


                        //ActionForAll.alertUserWithCloseActivity("VahanWire","Request cancelled !","OK", BookingStatusMechanic.this);
                    } else {
                        Log.e(TAG, " else  " + bookingStatus.getMessage());
                        //ActionForAll.alertUserWithCloseActivity("VahanWire", bookingStatus.getMessage(), "OK", BookingStatusMechanic.this);
                    }
                } else {
                    Log.e(TAG, " else  " + "Network busy please try after sometime");
                    //ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", MyForeGroundService.this);
                }
            }

            @Override
            public void onFailure(Call<Logout> call, Throwable t) {

                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });

    }
}
