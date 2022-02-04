package com.example.wearostest;

public class ThreeAxisSensor {
    double x, y, z;
    private int type;

    public ThreeAxisSensor(double x, double y, double z, int type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    private double[] getData() {
        double[] data = new double[7];
        data[Data.X] = x;
        data[Data.Y] = y;
        data[Data.Z] = z;

        data[Data.M] = Math.sqrt((x * x) + (y * y) + (z * z));
        data[Data.XY] = Math.sqrt((x * x) + (y * y));
        data[Data.YZ] = Math.sqrt((y * y) + (z * z));
        data[Data.XZ] = Math.sqrt((x * x) + (z * z));

        return data;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        double[] data = getData();
        return data[Data.X] + " " + data[Data.Y] + " " + data[Data.Z] + " " +
                data[Data.M] + " " + data[Data.XY] + " " + data[Data.YZ] + " " + data[Data.XZ];
//        return x + " " + y + " " + z;
    }
}
