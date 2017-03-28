package io.github.jb_aero.sensorapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    EditText ax, ay, az;
    Sensor accelerometer;
    SensorManager sm;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        ax = (EditText) findViewById(R.id.accelx);
        ay = (EditText) findViewById(R.id.accely);
        az = (EditText) findViewById(R.id.accelz);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        handler.post(new SensorWork(event));
    }

    private class SensorWork implements Runnable {

        SensorEvent e;

        SensorWork(SensorEvent event) {
            e = event;
        }

        @Override
        public void run() {
            ax.setText(String.valueOf(e.values[0]));
            ay.setText(String.valueOf(e.values[1]));
            az.setText(String.valueOf(e.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
