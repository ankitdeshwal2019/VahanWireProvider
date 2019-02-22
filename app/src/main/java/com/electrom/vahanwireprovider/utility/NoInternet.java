package com.electrom.vahanwireprovider.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.electrom.vahanwireprovider.R;


public class NoInternet extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public TextView yes, no;

    public NoInternet(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.internet_connection_layout);
        yes = findViewById(R.id.btn_yes);
        no = findViewById(R.id.btn_no);
        getWindow().getAttributes().windowAnimations = R.style.diologIntertnet;
        setCancelable(false);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                c.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}