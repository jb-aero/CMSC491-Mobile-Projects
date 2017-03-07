package io.github.jb_aero.averagerms;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

public class MyService extends Service implements SensorEventListener {

	MyBinder myBinder = new MyBinder();
	SensorManager sensorManager;
	Sensor accelerometer;
	List<float[]> data;

	public MyService() {
		data = new ArrayList<>();
	}

	@Override
	public IBinder onBind(Intent intent) {
		sensorManager = (SensorManager) getSystemService(MainActivity.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
		return myBinder;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
			return;
		}

		data.add(event.values);
		if (data.size() > 100) {
			data.remove(0);
		}
	}

	public float pollAverage() {

		float sum = 0;
		for (float[] item : data) {
			sum += RMS(item);
		}

		return sum / data.size();
	}

	/**
	 * Must be size 3 (or more, I guess)
	 * @param item
	 * @return
	 */
	public float RMS(float[] item) {
		return (float) Math.sqrt(Math.pow(item[0], 2) + Math.pow(item[1], 2) + Math.pow(item[2], 2));
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public class MyBinder extends Binder {
		MyService getService() {
			return MyService.this;
		}
	}
}
