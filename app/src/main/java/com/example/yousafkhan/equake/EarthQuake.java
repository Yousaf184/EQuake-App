package com.example.yousafkhan.equake;

public class EarthQuake {

    private double magnitude;
    private String location;
    private long time;
    private String detailURL;

    public EarthQuake(double magnitude, String location, long time, String detailUrl) {
        this.magnitude = magnitude;
        this.location = location;
        this.time = time;
        this.detailURL = detailUrl;
    }

    public double getMagnitude() {
        return this.magnitude;
    }

    public String getLocation() {
        return this.location;
    }

    public long getTime() {
        return this.time;
    }

    public String getDetailURL() {
        return this.detailURL;
    }
}
