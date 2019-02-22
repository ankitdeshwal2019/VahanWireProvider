package com.electrom.vahanwireprovider.utility;

import android.app.ProgressDialog;
import android.content.Context;

public class Util {

    public static ProgressDialog showProgressDialog(Context context) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }

    public static void hideProgressDialog(ProgressDialog pDialog) {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
