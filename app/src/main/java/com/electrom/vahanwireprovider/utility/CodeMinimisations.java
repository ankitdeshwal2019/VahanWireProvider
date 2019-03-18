package com.electrom.vahanwireprovider.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.MainActivity;
import com.electrom.vahanwireprovider.features.BookingHistory;
import com.electrom.vahanwireprovider.features.BookingStatusMechanic;
import com.electrom.vahanwireprovider.features.FacilitynPaymentMethod;
import com.electrom.vahanwireprovider.features.OfferActivity;
import com.electrom.vahanwireprovider.features.PaymentMethod;
import com.electrom.vahanwireprovider.features.ProfileActivity;
import com.electrom.vahanwireprovider.features.SelectBrand;
import com.electrom.vahanwireprovider.features.SelectIssue;
import com.electrom.vahanwireprovider.features.WorkingHours;
import com.electrom.vahanwireprovider.ragistration.BeforeLogin;
import com.electrom.vahanwireprovider.ragistration.ProviderLogin;

/**
 * Created by itrs on 11/22/2016.
 */

public class CodeMinimisations {

    private static FragmentActivity myContext;
    static SessionManager sessionManager;
    public CodeMinimisations(FragmentActivity myContext){
        this.myContext = myContext;

    }

    public static void largeSwitchCase(Context context, String menuItem){
        sessionManager = SessionManager.getInstance(context);

        Intent in = null;

        switch (menuItem) {

            case "Booking History":
                in = new Intent(context, BookingHistory.class);
                context.startActivity(in);
                break;

            case "Home":
                // ActionForAll.myFlash(context, "home page");
                in = new Intent(context, MainActivity.class);
                context.startActivity(in);
                break;

            case "Profile":
                // ActionForAll.myFlash(context, "Profile page");
                in = new Intent(context, ProfileActivity.class);
                context.startActivity(in);
                break;

            case "Offers":
                // ActionForAll.myFlash(context, "Offer page");
                in = new Intent(context, OfferActivity.class);
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

                case "Select Serviceable brand":
                // ActionForAll.myFlash(context, "Facility and Payment method page");
                in = new Intent(context, SelectBrand.class);
                context.startActivity(in);
                break;

                case "Select Services":
                // ActionForAll.myFlash(context, "Facility and Payment method page");
                in = new Intent(context, SelectIssue.class);
                context.startActivity(in);
                break;

            case "Log out":
                logoutAlert(context);
            break;
        }
    }

    private static void logoutAlert(final Context context)
    {
        new AlertDialog.Builder(context)
                .setTitle("LogOut")
                .setMessage("Do you want to logout?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sessionManager.setString(SessionManager.PROVIDER_MOBILE,"");
                        sessionManager.setString(SessionManager.PROVIDER_PIN, "");
                        sessionManager.setString(SessionManager.REGISTER_NAME, "");
                        sessionManager.setString(SessionManager.EMAIL, "");
                        sessionManager.setString(SessionManager.ADDRESS, "");
                        sessionManager.setString(SessionManager.CONTACT_PERSON,"");
                        sessionManager.setString(SessionManager.LANDLINE,"");
                        sessionManager.setString(SessionManager.LATITUDE, "");
                        sessionManager.setString(SessionManager.LONGITUDE, "");
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE,"");
                        sessionManager.setString(SessionManager.LOGIN_STATUS,"");
                        sessionManager.setString(SessionManager.SERVICE,"");
                        sessionManager.setString(SessionManager.DEVICE_ID,"");
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

                        dialog.dismiss();
                        Intent logout= new Intent(context, BeforeLogin.class);
                        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(logout);

                        ((Activity)context).finish();

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
}
