package com.example.wearostest;

public class Event {
    private Accelerometer accelerometer;
    private Gyroscope gyroscope;
    private LinearAccelerometer linearAccelerometer;
    private Gravity gravity;
    private long timestamp;
    public Event(long timestamp) {
        this.timestamp = timestamp;
    }

    public Accelerometer getAccelerometer() {
        return accelerometer;
    }

    public Gyroscope getGyroscope() {
        return gyroscope;
    }

    public LinearAccelerometer getLinearAccelerometer() {
        return linearAccelerometer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setAccelerometer(Accelerometer accelerometer) {
        this.accelerometer = accelerometer;
    }

    public void setGyroscope(Gyroscope gyroscope) {
        this.gyroscope = gyroscope;
    }

    public void setLinearAccelerometer(LinearAccelerometer linearAccelerometer) {
        this.linearAccelerometer = linearAccelerometer;
    }

    public Gravity getGravity() {
        return gravity;
    }

    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
