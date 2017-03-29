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
    double cgx, cgy, cgz, // current values (t)
            lgx, lgy, lgz; // last values (t - 1)

    public MyService() {
        binder = new MyBinder();
        lgx = lgy = lgz = 0;
    }

    public double pollGravity() {

        return Math.sqrt((cgx * cgx) + (cgy * cgy) + (cgz * cgz));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        cgx = (0.9 * lgx) + (0.1 * event.values[0]);
        cgy = (0.9 * lgy) + (0.1 * event.values[1]);
        cgz = (0.9 * lgz) + (0.1 * event.values[2]);
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
