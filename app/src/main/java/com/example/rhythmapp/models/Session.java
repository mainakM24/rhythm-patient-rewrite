package com.example.rhythmapp.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Session {
    public String patient_id;
    public String session_id;
    public String start_date;
    public String end_date;
    public int total_analyzed_duration;
    public int total_analyzed_beat;
    public int min_bpm;
    public int med_bpm;
    public int max_bpm;
    public int nb_count;
    public int lbbbb_count;
    public int rbbbb_count;
    public int apb_count;
    public int aapb_count;
    public int npb_count;
    public int spb_count;
    public int pvc_count;
    public int fvn_count;
    public int svf_count;
    public int vf_count;
    public int evf_count;
    public int aeb_count;
    public int neb_count;
    public int veb_count;
    public int pb_count;
    public int fpnb_count;
    public int ncpwb_count;
    public int ub_count;
    public int iqab_count;

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getStart_date() {
        return formatDate(start_date);
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return formatDate(end_date);
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getTotal_analyzed_duration() {
        return total_analyzed_duration;
    }

    public void setTotal_analyzed_duration(int total_analyzed_duration) {
        this.total_analyzed_duration = total_analyzed_duration;
    }

    public int getTotal_analyzed_beat() {
        return total_analyzed_beat;
    }

    public void setTotal_analyzed_beat(int total_analyzed_beat) {
        this.total_analyzed_beat = total_analyzed_beat;
    }

    public int getMin_bpm() {
        return min_bpm;
    }

    public void setMin_bpm(int min_bpm) {
        this.min_bpm = min_bpm;
    }

    public int getMed_bpm() {
        return med_bpm;
    }

    public void setMed_bpm(int med_bpm) {
        this.med_bpm = med_bpm;
    }

    public int getMax_bpm() {
        return max_bpm;
    }

    public void setMax_bpm(int max_bpm) {
        this.max_bpm = max_bpm;
    }

    public int getNb_count() {
        return nb_count;
    }

    public void setNb_count(int nb_count) {
        this.nb_count = nb_count;
    }

    public int getLbbbb_count() {
        return lbbbb_count;
    }

    public void setLbbbb_count(int lbbbb_count) {
        this.lbbbb_count = lbbbb_count;
    }

    public int getRbbbb_count() {
        return rbbbb_count;
    }

    public void setRbbbb_count(int rbbbb_count) {
        this.rbbbb_count = rbbbb_count;
    }

    public int getApb_count() {
        return apb_count;
    }

    public void setApb_count(int apb_count) {
        this.apb_count = apb_count;
    }

    public int getAapb_count() {
        return aapb_count;
    }

    public void setAapb_count(int aapb_count) {
        this.aapb_count = aapb_count;
    }

    public int getNpb_count() {
        return npb_count;
    }

    public void setNpb_count(int npb_count) {
        this.npb_count = npb_count;
    }

    public int getSpb_count() {
        return spb_count;
    }

    public void setSpb_count(int spb_count) {
        this.spb_count = spb_count;
    }

    public int getPvc_count() {
        return pvc_count;
    }

    public void setPvc_count(int pvc_count) {
        this.pvc_count = pvc_count;
    }

    public int getFvn_count() {
        return fvn_count;
    }

    public void setFvn_count(int fvn_count) {
        this.fvn_count = fvn_count;
    }

    public int getSvf_count() {
        return svf_count;
    }

    public void setSvf_count(int svf_count) {
        this.svf_count = svf_count;
    }

    public int getVf_count() {
        return vf_count;
    }

    public void setVf_count(int vf_count) {
        this.vf_count = vf_count;
    }

    public int getEvf_count() {
        return evf_count;
    }

    public void setEvf_count(int evf_count) {
        this.evf_count = evf_count;
    }

    public int getAeb_count() {
        return aeb_count;
    }

    public void setAeb_count(int aeb_count) {
        this.aeb_count = aeb_count;
    }

    public int getNeb_count() {
        return neb_count;
    }

    public void setNeb_count(int neb_count) {
        this.neb_count = neb_count;
    }

    public int getVeb_count() {
        return veb_count;
    }

    public void setVeb_count(int veb_count) {
        this.veb_count = veb_count;
    }

    public int getPb_count() {
        return pb_count;
    }

    public void setPb_count(int pb_count) {
        this.pb_count = pb_count;
    }

    public int getFpnb_count() {
        return fpnb_count;
    }

    public void setFpnb_count(int fpnb_count) {
        this.fpnb_count = fpnb_count;
    }

    public int getNcpwb_count() {
        return ncpwb_count;
    }

    public void setNcpwb_count(int ncpwb_count) {
        this.ncpwb_count = ncpwb_count;
    }

    public int getUb_count() {
        return ub_count;
    }

    public void setUb_count(int ub_count) {
        this.ub_count = ub_count;
    }

    public int getIqab_count() {
        return iqab_count;
    }

    public void setIqab_count(int iqab_count) {
        this.iqab_count = iqab_count;
    }

    public static String formatDate(String isoDate) {
        try {
            // Define the input format (ISO 8601)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

            // Parse the input date string into a Date object
            Date date = inputFormat.parse(isoDate);

            // Define the output format (05-JAN-2025 04:58PM)
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mma", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getDefault()); // Use the default timezone

            // Format the Date object into the desired output format
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date"; // Handle parsing errors
        }
    }
}
