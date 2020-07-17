package com.example.sensorperiodic;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import static android.hardware.SensorManager.SENSOR_DELAY_FASTEST;

public class MySensorService extends Service implements SensorEventListener {
    private static final String TAG = MySensorService.class.getCanonicalName();
    private SensorManager sensorManager;

    private Sensor pressureSensor,proximitySensor,lightSensor;

    private boolean lightSensorListenerRegistered,pressureSensorListenerRegistered,
                                        proximitySensorListenerRegistered,sensorValueChanged=false;

    public float[] val_lightsensor,val_proximitysensor,val_pressuresensor;

    private Thread backgroundService_Thread;

    public MySensorService() {
    }

    private void Sensor_initialize () {

        Log.d(TAG, "Initializing Sensor Services");

        // Get a reference to a SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // pressure sensor
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        // proximity sensor
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        // Light sensor
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        val_pressuresensor= new float[10000];
        val_lightsensor=new float[10000];
        val_proximitysensor=new float[10000];
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        float currentValue = sensorEvent.values[0];


        switch (sensorType) {
            // Event came from the light sensor.
            case Sensor.TYPE_LIGHT:
                // Handle light sensor
                val_lightsensor[0] = currentValue;
                MainActivity.mTextsensorLight.setText(String.format("%s%s", getResources().getString(R.string.label_light), val_lightsensor[0]));
                break;
            case Sensor.TYPE_PROXIMITY:
                //Handle proximity sensor
                val_proximitysensor[0]=currentValue;
                MainActivity.mTextsensorLight.setText(String.format("%s%s", getResources().getString(R.string.label_proximity), val_proximitysensor[0]));
            case Sensor.TYPE_PRESSURE:
                //Handle pressure sensor
                val_pressuresensor[0]=currentValue;
                MainActivity.mTextsensorLight.setText(String.format("%s%s", getResources().getString(R.string.pressure_sensor_hpa_1_2f), val_pressuresensor[0]));
            default:

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    //NULL
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String message = bundle.getString("SensorValue");
            MainActivity.mTextsensorLight.setText(getResources().getString(
                    R.string.label_light,val_lightsensor[0]));
            MainActivity.mTextSensorProximity.setText(getResources().getString(R.string.label_proximity,val_proximitysensor[0]));
            MainActivity.mTextSensorPressure.setText(getResources().getString(R.string.pressure_sensor_hpa_1_2f,val_pressuresensor[0]));
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Sensor_initialize ();
        // Register all available sensor listeners
        RegisterSensorListeners();
        startBackgroundThread(intent, startId);
        return START_STICKY;
    }


    private void startBackgroundThread(Intent intent, final int startId) {
        // Start a new thread for background operations
        backgroundService_Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                /*thread will run until Runtime exits,
                it completes its job, or throws an exception*/
                //if (sensorValueChanged) {
                    //  new sensor changed
                    //sensorValueChanged = false;
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("SensorValue", "Sensor updated: " + startId);

                    msg.setData(bundle);
                    Log.i(TAG, "Sending message to handler...");
                    handler.sendMessage(msg);
                }
            //}
        });

        backgroundService_Thread.setPriority(
                Process.THREAD_PRIORITY_BACKGROUND);
        Log.i(TAG, "Starting thread...");
        backgroundService_Thread.start();
    }


    private void UnregisterSensorListeners() {
        // Unregister accelerometer sensor listener
        if (proximitySensorListenerRegistered) {
            Log.d(TAG, "Unregistered proximity sensor listener");
            sensorManager.unregisterListener(this);
        }
        // Unregister gyroscope sensor listener
        if (pressureSensorListenerRegistered) {
            Log.d(TAG, "Unregistered Pressure sensor listener");
            sensorManager.unregisterListener(this);
        }
        // Unregister light sensor listener
        if (lightSensorListenerRegistered) {
            Log.d(TAG, "Unregistered light sensor listener");
            sensorManager.unregisterListener(this);
        }

    }

    private void RegisterSensorListeners() {
        // Register accelerometer sensor listener
        if (sensorManager.registerListener(this, pressureSensor, SENSOR_DELAY_FASTEST)) {
            Log.d(TAG, "Registered pressure sensor listener");
            pressureSensorListenerRegistered = true;
        }

        // Register gyroscope sensor listener
        if (sensorManager.registerListener(this, proximitySensor, SENSOR_DELAY_FASTEST)) {
            Log.d(TAG, "Registered proximity sensor listener");
            proximitySensorListenerRegistered = true;
        }

        // Register light sensor listener
        if (sensorManager.registerListener(this, lightSensor, SENSOR_DELAY_FASTEST)) {
            Log.d(TAG, "Registered light sensor listener");
            lightSensorListenerRegistered = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Register all available sensor listeners
        UnregisterSensorListeners();
      /*  if (backgroundServiceThread != null) {
            Log.i(TAG, "Destroying Thread...");
            Thread dummy = backgroundServiceThread;
            backgroundServiceThread = null;
            dummy.interrupt();
        } */
    }

}
