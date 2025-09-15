package com.example.rhythmapp.models;

import static com.example.rhythmapp.models.Session.formatDate;

public class GranularSession {
    public String start_date;
    public String end_date;
    public int total_analyzed_duration;
    public int total_analyzed_beat;
    public int bpm;
    public int nb_count;
    public int apb_count;
    public String hidstart_date;
    public String hidend_date;

    public String getStart_date() {
        return formatDate(start_date);
    }
    public String getEnd_date() {
        return formatDate(end_date);
    }
    public int getTotal_analyzed_duration() {
        return total_analyzed_duration;
    }
    public int getTotal_analyzed_beat() {
        return total_analyzed_beat;
    }
    public int getBpm() {
        return bpm;
    }
    public int getNb_count() {
        return nb_count;
    }
    public int getApb_count() {
        return apb_count;
    }
    public String getHidstart_date() {
        return hidstart_date;
    }
    public String getHidend_date() {
        return hidend_date;
    }
}
