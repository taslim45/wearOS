package com.example.wearostest;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Output;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.wearostest.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity implements SensorEventListener {
    private final String TAG = "WearOSTest";
    private TextView mTextView;
    private ActivityMainBinding binding;
    private final int MY_PERMISSIONS_REQUEST_BODY_SENSOR = 100;
    private final int MY_PERMISSIONS_WRITE_STORAGE = 200;
    SensorManager mSensorManager;
    private final int SENSOR_THREE = 3;
    private final int SENSOR_FIVE = 5;
    private final int SENSOR_FOUR = 4;
    private int SENSOR_RUN = SENSOR_THREE;
    private final int[] sensors = { Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_LINEAR_ACCELERATION, Sensor.TYPE_GRAVITY, Sensor.TYPE_ROTATION_VECTOR };
    private Data data;

    private Button mButtonStart, mButtonStop;

    private int dataCounter;

    private int receivedMessageNumber = 1;
    private int sentMessageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mTextView = binding.text;
        mButtonStart = binding.buttonStart;
        mButtonStop = binding.buttonStop;
        mButtonStop.setClickable(false);
//        Log.d(TAG, "1");
        permissionRequest();

        data = new Data();

        IntentFilter newFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);
    }

    private void writeData() {
        dataCounter++;
        Charset charset = Charset.forName("ASCII");
        StringBuilder filenameBuilder = new StringBuilder();
        filenameBuilder.append(dataCounter).append('_').append("data.csv");
        String filename = filenameBuilder.toString();
        String fileContents = data.getStringData();
        try (FileOutputStream fos = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes(charset));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        data.print();
    }

    private void writeGyroData() {
        Charset charset = Charset.forName("ASCII");

        String filename = "gyro.txt";
        String fileContents = data.getGyroStringData();
        try (FileOutputStream fos = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes(charset));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        dataCounter = 0;
    }

    private void printSensors() {
        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));

        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensor : sensorList) {
            Log.d("WearOSTest", "Name: " + sensor.getName() + ", Type String: " + sensor.getStringType() + ", Type number: " + sensor.getType());
        }
    }

    private void setSensors() {
        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        for(int index = 0; index < Math.min(sensors.length, SENSOR_RUN); index++) {
            mSensorManager.registerListener(MainActivity.this, mSensorManager.getDefaultSensor(sensors[index]), SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private void permissionRequest() {
        Log.d(TAG, "2");
        if (checkSelfPermission(Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.BODY_SENSORS},
                    MY_PERMISSIONS_REQUEST_BODY_SENSOR);
        }

        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    MY_PERMISSIONS_WRITE_STORAGE
            );
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged: sensor value changed");
        long timestamp = event.timestamp;
//        Log.d(TAG, "onSensorChanged: " + timestamp);
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            data.insertEvent(timestamp, new Accelerometer(event.values[Data.X], event.values[Data.Y], event.values[Data.Z], Sensor.TYPE_ACCELEROMETER));
        }
        else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//            Log.d(TAG, "onSensorChanged: " + event.values[Data.X] + ", " + event.values[Data.Y] + ", " + event.values[Data.Z]);
            data.insertEvent(timestamp, new Gyroscope(event.values[Data.X], event.values[Data.Y], event.values[Data.Z], Sensor.TYPE_GYROSCOPE));
        }
        else if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            data.insertEvent(timestamp, new LinearAccelerometer(event.values[Data.X], event.values[Data.Y], event.values[Data.Z], Sensor.TYPE_LINEAR_ACCELERATION));
        }
        else if(event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            data.insertEvent(timestamp, new Gravity(event.values[Data.X], event.values[Data.Y], event.values[Data.Z], Sensor.TYPE_GRAVITY));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, sensor.getName() + " " + accuracy);
    }

    public void startClicked(View view) {
        mButtonStop.setClickable(true);
        mButtonStart.setClickable(false);
        Log.d(TAG, "startClicked:");
        data.clean();
        setSensors();
//        sendMessageToPhone();
    }

    public void stopClicked(View view) {
        mButtonStop.setClickable(false);
        mButtonStart.setClickable(true);
        Log.d(TAG, "stopClicked:");

        unRegisterSensors();
        writeData();
//        writeGyroData();
    }

    private void unRegisterSensors() {
        if(mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            Log.d(TAG, "unregistering listener");
        }
    }

    // Data Layer changes

    private void sendMessageToPhone() {
        String message = "Send message to phone: " + sentMessageNumber++;
        mTextView.setText(message);

        String datapath = "/my_path";
        new SendMessageThread(datapath, message).start();
    }
    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = "Received from phone: " + receivedMessageNumber++;
            mTextView.setText(message);
        }
    }

    private class SendMessageThread extends Thread {
        String path, message;
        SendMessageThread(String path, String message) {
            this.path = path;
            this.message = message;
        }

        @Override
        public void run() {
            Log.d(TAG, "run: thread 1");
            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                List<Node> nodes = Tasks.await(nodeListTask);
                for(Node node : nodes) {
                    Task<Integer> sendMessageTask = Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    Integer result = Tasks.await(sendMessageTask);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }
    }
}