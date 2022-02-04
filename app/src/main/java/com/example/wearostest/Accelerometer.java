package com.example.wearostest;

import androidx.annotation.NonNull;

public class Accelerometer extends ThreeAxisSensor {

    public Accelerometer(double x, double y, double z, int type) {
        super(x, y, z, type);
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
