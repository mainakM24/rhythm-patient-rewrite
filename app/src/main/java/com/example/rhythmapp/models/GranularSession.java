package com.example.rhythmapp.models;

import static com.example.rhythmapp.models.Session.formatDate;

public class GranularSession {
    private String start_date;
    private String end_date;
    private int total_analyzed_duration;
    private int total_analyzed_beat;
    private int bpm;
    private int nb_count;
    private int apb_count;
    private String hidstart_date;
    private String hidend_date;

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

    public int getApb_count() {
        return apb_count;
    }

    public void setApb_count(int apb_count) {
        this.apb_count = apb_count;
    }

    public String getHidstart_date() {
        return hidstart_date;
    }

    public void setHidstart_date(String hidstart_date) {
        this.hidstart_date = hidstart_date;
    }

    public String getHidend_date() {
        return hidend_date;
    }

    public void setHidend_date(String hidend_date) {
        this.hidend_date = hidend_date;
    }
}
