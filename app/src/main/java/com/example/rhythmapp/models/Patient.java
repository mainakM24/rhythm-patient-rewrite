package com.example.rhythmapp.models;

public class Patient {
    public int id;
    public String patient_id;
    public String p_password;
    public String p_name;
    public String p_dob;
    public String p_sex;
    public String p_address;
    public String p_mobile;
    public String p_remarks;
    public String p_start_date;
    public String p_status;
    public String p_notes;
    public String p_street_name;
    public String p_house_no;
    public String p_city;
    public String p_pin_cdoe;
    public String p_state;
    public String p_country;
    public String p_email;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPatient_id() {
        return patient_id;
    }
    public String getP_name() {
        return p_name;
    }
    public String getP_dob() {
        return p_dob;
    }
    public String getP_sex() {
        return p_sex;
    }
    public String getP_mobile() {
        return p_mobile;
    }
    public String getP_start_date() {
        return p_start_date;
    }
    public String getP_notes() {
        return p_notes;
    }
    public String getP_street_name() {
        return p_street_name;
    }
    public String getP_house_no() {
        return p_house_no;
    }
    public String getP_city() {
        return p_city;
    }
    public String getP_pin_cdoe() {
        return p_pin_cdoe;
    }
    public String getP_state() {
        return p_state;
    }
    public String getP_country() {
        return p_country;
    }
    public String getP_email() {
        return p_email;
    }
}
