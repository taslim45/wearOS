package com.example.wearostest;

import android.hardware.Sensor;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Data {
    private static final String TAG = "WearOSTest";
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
    public static final int M = 3;
    public static final int XY = 4;
    public static final int YZ = 5;
    public static final int XZ = 6;

    private Map<Long, Event> timeEventMap;

    public Data() {
        timeEventMap = new TreeMap<>();
    }

    public void clean() {
        timeEventMap.clear();
    }

    public void insertEvent(long timestamp, ThreeAxisSensor sensor) {
        Event event = new Event(timestamp);

        if(timeEventMap.containsKey(timestamp)) {
            event = timeEventMap.get(timestamp);
        }
        else timeEventMap.put(timestamp, event);

        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) event.setAccelerometer((Accelerometer) sensor);
        else if(sensor.getType() == Sensor.TYPE_GYROSCOPE) event.setGyroscope((Gyroscope) sensor);
        else if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) event.setLinearAccelerometer((LinearAccelerometer) sensor);
        else if(sensor.getType() == Sensor.TYPE_GRAVITY) event.setGravity((Gravity) sensor);
    }

    public void print() {
        for(Map.Entry<Long, Event> entry : timeEventMap.entrySet()) {
            long time = entry.getKey();
            Event event = entry.getValue();
            Log.d(TAG, String.valueOf(time) + ": " + event.getAccelerometer().toString() + ", " + event.getGyroscope().toString() + ", " + event.getLinearAccelerometer().toString());
        }
    }

    public String getStringData() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Long, Event> entry : timeEventMap.entrySet()) {
            long time = entry.getKey();
            Event event = entry.getValue();
//            Log.d(TAG, String.valueOf(time) + ": " + event.getAccelerometer().toString() + ", " + event.getGyroscope().toString() + ", " + event.getLinearAccelerometer().toString());
            if(event.getAccelerometer() != null && event.getGyroscope() != null && event.getLinearAccelerometer() != null) {
                sb.append(String.valueOf(time)).append(" ");
                sb.append(event.getAccelerometer().toString()).append(" ");
                sb.append(event.getGyroscope().toString()).append(" ");
                sb.append(event.getLinearAccelerometer().toString());
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    public String getGyroStringData() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Long, Event> entry : timeEventMap.entrySet()) {
            long time = entry.getKey();
            Event event = entry.getValue();
//            Log.d(TAG, String.valueOf(time) + ": " + event.getAccelerometer().toString() + ", " + event.getGyroscope().toString() + ", " + event.getLinearAccelerometer().toString());
            if(event.getGyroscope() != null && event.getGravity() != null) {
                sb.append(String.valueOf(time)).append(" ");
                sb.append(event.getGyroscope().toString()).append(" ");
                sb.append(event.getGravity().toString());
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }
}
