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

    @SerializedName("sum(apb_count)")
    public int apb_count;

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
}
