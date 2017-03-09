package io.github.jb_aero.offlinegps;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.EditText;

/**
 * Created by bilbrey1 on 3/9/2017.
 */

public class Method2Logic implements SensorEventListener {

	EditText _lat2, _long2;
	float lat2, long2, rotx, roty, rotz;

	MainActivity main;

	Method2Logic(MainActivity mainActivity) {
		main = mainActivity;
		_lat2 = (EditText) main.findViewById(R.id.lat2);
		_long2 = (EditText) main.findViewById(R.id.long2);
	}
	
	private class AccelWork implements Runnable {

		@Override
		public void run() {

		}
	}

	private class GyroWork implements Runnable {

		@Override
		public void run() {

		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType())
		{
			case Sensor.TYPE_LINEAR_ACCELERATION:
				main.myHandler.post(new AccelWork());
				break;
			case Sensor.TYPE_GYROSCOPE:
				main.myHandler.post(new GyroWork());
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				break;
			default:
				break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}
