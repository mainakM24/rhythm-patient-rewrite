package com.example.rhythmapp.models;

public class EcgData {
    public int wpddd_id;
    public int seq_no;
    public String anamoly_flag;
    public double voltage;

    public int getWpddd_id() {
        return wpddd_id;
    }

    public void setWpddd_id(int wpddd_id) {
        this.wpddd_id = wpddd_id;
    }

    public int getSeq_no() {
        return seq_no;
    }

    public void setSeq_no(int seq_no) {
        this.seq_no = seq_no;
    }

    public String getAnamoly_flag() {
        return anamoly_flag;
    }

    public void setAnamoly_flag(String anamoly_flag) {
        this.anamoly_flag = anamoly_flag;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }
}
