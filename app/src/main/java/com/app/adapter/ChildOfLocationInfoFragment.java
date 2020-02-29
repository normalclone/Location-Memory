package com.app.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.dao.tLocationDAO;
import com.app.model.tLocation;
import com.app.util.DateTimeUtil;
import com.app.view.LocationDetailActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.util.Calendar;

public class ChildOfLocationInfoFragment extends RecyclerView.Adapter<ChildOfLocationInfoFragment.ViewHolder>{
    private Context mContext;
    private int locationId;
    private LocationDetailActivity activity;

    public ChildOfLocationInfoFragment(Context mContext, int locationId, LocationDetailActivity activity){
        this.mContext = mContext;
        this.locationId = locationId;
        this.activity =activity;
    }
    @NonNull
    @Override
    public ChildOfLocationInfoFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildOfLocationInfoFragment.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_location_info, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChildOfLocationInfoFragment.ViewHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
        private TextInputLayout edtlo_locationName;
        private TextInputLayout edtlo_createdAt;

        private TextInputEditText edt_locationName;
        private TextInputEditText edt_createdAt;

        private MaterialButton btn_save;
        private MaterialButton btn_navigation;
        tLocation location;

        private int yy, MM, dd;
        private int year, month, day, hour, minute;

        ViewHolder(View view) {
            super(view);
            edtlo_locationName = view.findViewById(R.id.edtlo_location_name);
            edtlo_createdAt = view.findViewById(R.id.edtlo_created_at);
            edt_createdAt = view.findViewById(R.id.edt_created_at);
            edt_locationName = (TextInputEditText) view.findViewById(R.id.edt_location_name);
            btn_save = view.findViewById(R.id.btn_save_location);
            btn_navigation = view.findViewById(R.id.btn_director);
        }

        void bind(ViewHolder holder,int position) {
            location = new tLocationDAO(mContext).get(locationId);
            edt_locationName.setText(location.getLocationName()+"");
            edt_createdAt.setText(DateTimeUtil.convertDatetimeToString(location.getCreated_at()));

            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            edtlo_createdAt.setEndIconOnClickListener(removedClick);
            btn_save.setOnClickListener(enableEdit);
            btn_navigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.closeAndStartNavigator();
                }
            });
        }

        private View.OnClickListener navigation = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                btn_save.setText(mContext.getString(R.string.confirm));
                btn_save.setOnClickListener(changeLocationInformation);
                edtlo_locationName.setEnabled(true);
                edtlo_createdAt.setEndIconOnClickListener(pickDateTimeClicked);
            }
        };

        private View.OnClickListener enableEdit = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                btn_save.setText(mContext.getString(R.string.confirm));
                btn_save.setOnClickListener(changeLocationInformation);
                edtlo_locationName.setEnabled(true);
                edtlo_createdAt.setEndIconOnClickListener(pickDateTimeClicked);
            }
        };

        private View.OnClickListener changeLocationInformation = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                location.setLocationName(edt_locationName.getText()+"");
                try {
                    location.setCreated_at(DateTimeUtil.convertStringToDatetime(edt_createdAt.getText()+""));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(new tLocationDAO(mContext).save(location)){
                    btn_save.setText(mContext.getString(R.string.edit_this_location));
                    btn_save.setOnClickListener(enableEdit);
                    edtlo_locationName.setEnabled(false);
                    edtlo_createdAt.setEndIconOnClickListener(removedClick);
                    Toast.makeText(mContext, mContext.getString(R.string.change_success), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(mContext, mContext.getString(R.string.change_fail), Toast.LENGTH_LONG).show();
                }
            }
        };

        private View.OnClickListener removedClick = new View.OnClickListener() {@Override public void onClick(View v) {}};

        private View.OnClickListener pickDateTimeClicked = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, ViewHolder.this, year, month, day);
                datePickerDialog.show();
            }
        };

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            yy = year;
            MM = month+1;
            dd = dayOfMonth;
            TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, this, hour, minute, true);
            timePickerDialog.show();
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String rs = yy+"/"+ DateTimeUtil.checkPickedNumber(MM)+"/"+DateTimeUtil.checkPickedNumber(dd)+" "+DateTimeUtil.checkPickedNumber(hourOfDay)+":"+DateTimeUtil.checkPickedNumber(minute)+":00";
            edt_createdAt.setText(rs);
        }
    }
}
