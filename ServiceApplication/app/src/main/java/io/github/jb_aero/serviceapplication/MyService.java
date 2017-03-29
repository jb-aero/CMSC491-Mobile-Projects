package io.github.jb_aero.serviceapplication;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service implements SensorEventListener {

    MyBinder binder;
    double gx, gy, gz;

    public MyService() {
        binder = new MyBinder();
        gx = gy = gz = 0;
    }

    public double pollGravity() {

        return Math.sqrt((gx * gx) + (gy * gy) + (gz * gz));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gx = (0.9 * gx) + (0.1 * event.values[0]);
        gy = (0.9 * gy) + (0.1 * event.values[1]);
        gz = (0.9 * gz) + (0.1 * event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class MyBinder extends Binder
    {
        public MyService getService() {
            return MyService.this;
        }
    }
}
