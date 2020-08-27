package com.example.shake;

import java.text.DecimalFormat;

public class DateType {

    private double mag;
    private String place;
    private long time;

    public DateType(double mag,String place,long time){
        this.mag=mag;
        this.place=place;
        this.time=time;
    }

    public double getMag() {
        DecimalFormat decimalFormat=new DecimalFormat("0.0");
        return Double.parseDouble(decimalFormat.format(mag));
    }

    public long getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }
}
