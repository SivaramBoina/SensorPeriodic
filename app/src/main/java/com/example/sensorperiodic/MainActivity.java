package com.example.sensorperiodic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();
    public static TextView mTextSensorPressure,mTextsensorLight,mTextSensorProximity;
    private int timerControl=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextSensorPressure = findViewById(R.id.label_pressure);
        mTextsensorLight = findViewById(R.id.label_light);
        mTextSensorProximity = findViewById(R.id.label_proximity);
    }

    public void button_start_fun(View view) {
        timerControl=1;
        Log.i(TAG, "Start button is clicked");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (timerControl==1) {
                    Intent startServiceIntent = new Intent(MainActivity.this, MySensorService.class);
                    startService(startServiceIntent);
                   // new Handler().postDelayed(this, 1000);
                }
            }
        }, 1000);
    }


    public void button_stop_fun(View view) {
        timerControl=0;
        //Log.i(TAG, "Stop button is clicked");
        //stopService(new Intent(this, MySensorService.class));

        // Stop android service.
        Log.i(TAG, "Destroying service");
        Intent stopServiceIntent = new Intent(MainActivity.this, MySensorService.class);
        stopService(stopServiceIntent);
    }
}
