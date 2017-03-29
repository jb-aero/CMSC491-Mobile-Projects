package io.github.jb_aero.serviceapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText value;
    MyService service;
    SensorManager sm;
    Sensor accelerometer;
    boolean bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        value = (EditText) findViewById(R.id.value);
        bound = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bound) {
            unbindService(serviceConnection);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button && bound) {
			value.setText(String.valueOf(service.pollGravity()));
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((MyService.MyBinder) binder).getService();
            sm.registerListener(service, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sm.unregisterListener(service);
            bound = false;
        }
    };
}
