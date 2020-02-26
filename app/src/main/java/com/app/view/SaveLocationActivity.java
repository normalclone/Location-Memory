package com.app.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.R;
import com.app.dao.tLocationDAO;
import com.app.model.tLocation;
import com.app.util.DateTimeUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.util.Calendar;

public class SaveLocationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private TextInputLayout edtlo_locationName;
    private TextInputLayout edtlo_createdAt;

    private TextInputEditText edt_locationName;
    private TextInputEditText edt_createdAt;

    private int yy, MM, dd;
    private int year, month, day, hour, minute;

    private double longitude;
    private double latitude;
    private boolean isOldSaved;
    private tLocation oldSavedLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        longitude = this.getIntent().getDoubleExtra("longitude", 0);
        latitude = this.getIntent().getDoubleExtra("latitude", 0);
        isOldSaved = this.getIntent().getBooleanExtra("isOldSaved", false);
        //Element declaration
        edtlo_locationName = findViewById(R.id.edtlo_location_name);
        edtlo_createdAt = findViewById(R.id.edtlo_created_at);
        edt_createdAt = findViewById(R.id.edt_created_at);
        edt_locationName = findViewById(R.id.edt_location_name);
        MaterialButton btn_save = findViewById(R.id.btn_save_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {finish();}});

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        edt_createdAt.setText(year+"/"+DateTimeUtil.checkPickedNumber(month+1)+"/"+DateTimeUtil.checkPickedNumber(day)+" "+DateTimeUtil.checkPickedNumber(hour)+":"+DateTimeUtil.checkPickedNumber(minute)+":00");
        if(isOldSaved){
            oldSavedLocation = new tLocationDAO(SaveLocationActivity.this).get(this.getIntent().getIntExtra("oldSavedId", 1));
            edt_locationName.setText(oldSavedLocation.getLocationName());
            toolbar.setTitle(getString(R.string.save_location_toolbar_edit));
        }


        edtlo_createdAt.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SaveLocationActivity.this, SaveLocationActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });

        btn_save.setOnClickListener(btnSaveClicked);
    }

    private View.OnClickListener btnSaveClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean flag = true;
            String locationName = edt_locationName.getText()+"";
            if(locationName.equals("")){
                edtlo_locationName.setError(getString(R.string.err_location_required)); flag = false;
            }
            if(longitude == 0 || latitude == 0) flag = false;

            if(flag){
                try{
                    Location location = new Location("Current location");
                    location.setLongitude(longitude);
                    location.setLatitude(latitude);

                    if(isOldSaved){
                        if(new tLocationDAO(SaveLocationActivity.this).save(new tLocation(oldSavedLocation.getId(), locationName, location, DateTimeUtil.convertStringToDatetime(edt_createdAt.getText()+"")))){
                            Toast.makeText(SaveLocationActivity.this, getString(R.string.rs_success_override_location)+locationName+"", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }else{
                            Toast.makeText(SaveLocationActivity.this, getString(R.string.rs_fail_override_location)+locationName+"", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        if(new tLocationDAO(SaveLocationActivity.this).save(new tLocation(0, locationName, location, DateTimeUtil.convertStringToDatetime(edt_createdAt.getText()+"")))){
                            Toast.makeText(SaveLocationActivity.this, getString(R.string.rs_success_save_location)+locationName+"", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }else{
                            Toast.makeText(SaveLocationActivity.this, getString(R.string.rs_fail_override_location)+locationName+"", Toast.LENGTH_LONG).show();
                        }
                    }

                }catch (ParseException ex){
                    edtlo_createdAt.setError("Wrong datetime format!");
                    ex.printStackTrace();
                }
            }
        }
    };

    public void onBackPressed (){
        final RelativeLayout root = findViewById(R.id.root);
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                root.setVisibility(View.GONE);
                SaveLocationActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        root.startAnimation(anim);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yy = year;
        MM = month+1;
        dd = dayOfMonth;
        TimePickerDialog timePickerDialog = new TimePickerDialog(SaveLocationActivity.this, SaveLocationActivity.this, hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String rs = yy+"/"+ DateTimeUtil.checkPickedNumber(MM)+"/"+DateTimeUtil.checkPickedNumber(dd)+" "+DateTimeUtil.checkPickedNumber(hourOfDay)+":"+DateTimeUtil.checkPickedNumber(minute)+":00";
        edt_createdAt.setText(rs);
    }
}
