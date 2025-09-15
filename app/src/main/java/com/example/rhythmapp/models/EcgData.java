package com.example.rhythmapp.models;

public class EcgData {
    public int seq_no;
    public String anamoly_flag;
    public double voltage;

    public int getSeq_no() {
        return seq_no;
    }
    public String getAnamoly_flag() {
        return anamoly_flag;
    }
    public double getVoltage() {
        return voltage;
    }
}
