package com.example.rhythmapp.models;

import static com.example.rhythmapp.models.Session.formatDate;

import com.google.gson.annotations.SerializedName;

public class DetailedSession {


    public String start_datetime;

    @SerializedName("min(start_date)")
    public String start_date;
    public String hidstart_date;

    @SerializedName("max(end_date)")
    public String end_date;
    public String hidend_date;
    public int total_analyzed_duration;

    @SerializedName("sum(total_analyzed_beat)")
    public int total_analyzed_beat;

    @SerializedName("avg(bpm)")
    public int bpm;

    @SerializedName("sum(nb_count)")
    public int nb_count;
    public int lbbbb_count;
    public int rbbbb_count;

    @SerializedName("sum(apb_count)")
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

    public String getStart_datetime() {
        return formatDate(start_datetime);
    }

    public void setStart_datetime(String start_datetime) {
        this.start_datetime = start_datetime;
    }

    public String getStart_date() {
        return formatDate(start_date);
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getHidstart_date() {
        return formatDate(hidstart_date);
    }

    public void setHidstart_date(String hidstart_date) {
        this.hidstart_date = hidstart_date;
    }

    public String getEnd_date() {
        return formatDate(end_date);
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getHidend_date() {
        return formatDate(hidend_date);
    }

    public void setHidend_date(String hidend_date) {
        this.hidend_date = hidend_date;
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

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
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
}
