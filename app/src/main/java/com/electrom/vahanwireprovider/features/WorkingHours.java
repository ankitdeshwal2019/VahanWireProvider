package com.electrom.vahanwireprovider.features;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CustomButton;

public class WorkingHours extends AppCompatActivity implements View.OnClickListener {

    String workingArrayStart[] = {"HH:MM","1:00","2:00","3:00","4:00","5:00","6:00","7:00","8:00","9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00","24:00"};
    String workingArrayEnd[] = {"HH:MM","1:00","2:00","3:00","4:00","5:00","6:00","7:00","8:00","9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00","24:00"};
    Spinner spinMandayDate,spinMandayEndDate,spinTuesdayDateStart,spinTuesdayDateEnd,spinWednesdayDateStart,
            spinWednesdayDateEnd,spinThrusdayDateStart,spinThrusdayDateEnd,spinFridayDateStart,spinFridayDateEnd,
            spinSaturdayDateStart,spinSaturdayDateEnd,spinSundayStart, spinSundayEndDate;
    ImageView iv_back_orking_hours;
    CustomButton btnSumbitWorkingHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_hours);
        initView();
    }

    private void initView() {
        ArrayAdapter<String> adapterStart = new ArrayAdapter(this, R.layout.item_spin, workingArrayStart);
        ArrayAdapter<String> adapterEnd = new ArrayAdapter(this, R.layout.item_spin, workingArrayEnd);
        spinMandayDate = findViewById(R.id.spinMandayDate);
        spinMandayEndDate = findViewById(R.id.spinMandayEndDate);
        spinTuesdayDateStart = findViewById(R.id.spinTuesdayDateStart);
        spinTuesdayDateEnd = findViewById(R.id.spinTuesdayDateEnd);
        spinWednesdayDateStart = findViewById(R.id.spinWednesdayDateStart);
        spinWednesdayDateEnd = findViewById(R.id.spinWednesdayDateEnd);
        spinThrusdayDateStart = findViewById(R.id.spinThrusdayDateStart);
        spinThrusdayDateEnd = findViewById(R.id.spinThrusdayDateEnd);
        spinFridayDateStart = findViewById(R.id.spinFridayDateStart);
        spinFridayDateEnd = findViewById(R.id.spinFridayDateEnd);
        spinSaturdayDateStart = findViewById(R.id.spinSaturdayDateStart);
        spinSaturdayDateEnd = findViewById(R.id.spinSaturdayDateEnd);
        spinSundayStart = findViewById(R.id.spinSundayStart);
        spinSundayEndDate = findViewById(R.id.spinSundayEndDate);
        iv_back_orking_hours = findViewById(R.id.iv_back_orking_hours);
        btnSumbitWorkingHours = findViewById(R.id.btnSumbitWorkingHours);
        iv_back_orking_hours.setOnClickListener(this);
        btnSumbitWorkingHours.setOnClickListener(this);

        spinMandayDate.setAdapter(adapterStart);
        spinMandayEndDate.setAdapter(adapterEnd);

        spinTuesdayDateStart.setAdapter(adapterStart);
        spinTuesdayDateEnd.setAdapter(adapterEnd);

        spinWednesdayDateStart.setAdapter(adapterStart);
        spinWednesdayDateEnd.setAdapter(adapterEnd);

        spinThrusdayDateStart.setAdapter(adapterStart);
        spinThrusdayDateEnd.setAdapter(adapterEnd);

        spinFridayDateStart.setAdapter(adapterStart);
        spinFridayDateEnd.setAdapter(adapterEnd);

        spinSaturdayDateStart.setAdapter(adapterStart);
        spinSaturdayDateEnd.setAdapter(adapterEnd);

        spinSundayStart.setAdapter(adapterStart);
        spinSundayEndDate.setAdapter(adapterEnd);

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.iv_back_orking_hours)
        {
            finish();
        }
        else if(v.getId()==R.id.btnSumbitWorkingHours)
        {
            ActionForAll.alertUserWithCloseActivity("VahanWire", "Work is in Progress", "OK", WorkingHours.this);
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
