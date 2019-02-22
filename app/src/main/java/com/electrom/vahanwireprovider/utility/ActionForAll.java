package com.electrom.vahanwireprovider.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.electrom.vahanwireprovider.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 7/20/2017.
 */

public class ActionForAll {

    static Context context;

    public ActionForAll(Context context) {
        this.context = context;
    }

    //============================================= Flash Massage ==============================================================//

    public static void myFlash(Context context, String name) {
        Toast.makeText(context, name, Toast.LENGTH_LONG).show();
    }

    //============================================= Button Clicking Effect =====================================================//

    public static void buttonClickEffect(View button) {

        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }


    //============================================================================================================================

    //=================================================>> Email Validator <<======================================================//

    public static boolean isValidEmail(EditText e , Activity activity) {
        if(!TextUtils.isEmpty(e.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(e.getText().toString()).matches()){
            return true;
        }
        else {
            e.setError("Please type valid email id!");
            requestFocus(e, activity);
            return false;
        }
    }

    //=========================================================================================================================//

    //=========================================>> Alert Dialog with one Action <<==============================================//


    public static void alertUser(String title, String massage, String action, final Activity context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(massage);
        builder.setIcon(R.drawable.ic_launcher_foreground);
        builder.setPositiveButton(action,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    public static void alertUserWithCloseActivity(String title, String massage, String action, final Activity context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(massage);
        builder.setPositiveButton(action,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // context.startActivity(new Intent(context , LoginActivity.class));
                context.finish();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public static void alertChoiseCloseActivity(final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("VahanWire");
        builder.setMessage("Do you want to exit from this page?");
        builder.setPositiveButton("YES",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((Activity)context).finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //=======================================================================================================================//

    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected())
            return true;

        else{
            new AlertDialog.Builder(context)
                    .setTitle("Network Info")
                    .setMessage("No Network Available, Please Check Internet Settings")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ((Activity)context).finish();
                        }
                    })
                    .create().show();
            return false;
        }

    }

    public static String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    /*
     * set font here
     * */

    public static void setFontText(TextView t, Context c)
    {
        Typeface typeface = Typeface.createFromAsset(c.getAssets(),"fonts/Raleway-Regular.ttf");
        t.setTypeface(typeface);
    }

    public static boolean validEditText(EditText edit , String text, Activity act) {
        if (edit.getText().toString().trim().length()< 3) {
            edit.setError("Please type " + text + " here!");
            requestFocus(edit, act);
            return false;
        }
        return true;
    }

    public static boolean validPassword(EditText edit , String text, Activity act) {
        if (edit.getText().toString().trim().length() < 6) {
            edit.setError("Please enter minimum 6 digit " + text + " here!");
            requestFocus(edit, act);
            return false;
        }
        return true;
    }

    public static boolean validMobileEditText(EditText edit , String text, Activity act) {
        if (edit.getText().toString().trim().length() != 10) {
            edit.setError("Please type " + text + " here!");
            requestFocus(edit, act);
            return false;
        }
        return true;
    }

    public static void requestFocus(View view, Activity activity) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static ProgressDialog showProgressDialog(Context context) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(true);
        pDialog.show();
        return pDialog;
    }

    public static void hideProgressDialog(ProgressDialog pDialog) {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
