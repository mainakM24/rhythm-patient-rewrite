package com.example.rhythmapp.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Doctor {
    public String doctor_id;
    public String d_name;
    public String pd_admission_date;
    public String pd_hospital_name;
    public String boarding_id;

    public String getDoctor_id() {
        return doctor_id;
    }
    public String getD_name() {
        return d_name;
    }
    public String getFormattedPd_admission_date() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            Date date = inputFormat.parse(pd_admission_date);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

            assert date != null;
            return outputFormat.format(date);
        } catch (ParseException e) {
            return "Invalid date";
        }
    }
    public String getPd_hospital_name() {
        return pd_hospital_name;
    }
    public String getBoarding_id() {
        return boarding_id;
    }
}
