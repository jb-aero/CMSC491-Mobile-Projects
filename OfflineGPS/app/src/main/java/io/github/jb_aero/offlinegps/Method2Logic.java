package io.github.jb_aero.offlinegps;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.EditText;

public class Method2Logic implements SensorEventListener {

	EditText _lat2, _long2, comx, comy, comz;
	float lat2, long2, rotx, roty, rotz;
	boolean hasLocation, hasCompass;
	long lastTime;

	MainActivity main;

	Method2Logic(MainActivity mainActivity) {
		main = mainActivity;
		_lat2 = (EditText) main.findViewById(R.id.lat2);
		_long2 = (EditText) main.findViewById(R.id.long2);

		hasLocation = false;
		hasCompass = false;
		lastTime = 0;

		comx = (EditText) main.findViewById(R.id.comx);
		comy = (EditText) main.findViewById(R.id.comy);
		comz = (EditText) main.findViewById(R.id.comz);
	}
	
	private class AccelWork extends SensorWork implements Runnable {

		public AccelWork(SensorEvent e) {
			super(e);
		}

		@Override
		public void run() {

			if (lastTime == 0)
			{
				lastTime = event.timestamp;
				return;
			}
			/*
			Here I need to apply acceleration to change the location

			long timeDelta = event.timestamp - lastTime;

			float distX = (float) ((event.values[0] / 2) * Math.pow(timeDelta, 2));
			float distZ = (float) ((event.values[2] / 2) * Math.pow(timeDelta, 2));

			lat2 = lat2 + distZ / main.EARTH_RADIUS;
			long2 = long2 + distX / main.EARTH_RADIUS;

			... it didn't work.
			*/

			_lat2.setText(String.valueOf(lat2));
			_long2.setText(String.valueOf(long2));

			main.updateDistance();
		}
	}

	private class GyroWork extends SensorWork implements Runnable {

		public GyroWork(SensorEvent e) {
			super(e);
		}

		@Override
		public void run() {

			/*
			So here I need to change the orientation of the phone so that acceleration will
			be applied in the correct directions
			 */
			main.updateDistance();
		}
	}

	private class CompassWork extends SensorWork implements Runnable {

		Method2Logic container;
		public CompassWork(SensorEvent e, Method2Logic container) {
			super(e);
			this.container = container;
		}

		@Override
		public void run() {

			/*
			Here I need to figure out the original orientation of the phone
			 */
			comx.setText(String.valueOf(event.values[0]));
			comy.setText(String.valueOf(event.values[1]));
			comz.setText(String.valueOf(event.values[2]));

			hasCompass = true;

			main.sensorManager.unregisterListener(container, main.compass);

			main.updateDistance();
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType())
		{
			case Sensor.TYPE_LINEAR_ACCELERATION:
				main.myHandler.post(new AccelWork(event));
				break;
			case Sensor.TYPE_GYROSCOPE:
				main.myHandler.post(new GyroWork(event));
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				// CompassWork unregisters listening, so this will only run once
				main.myHandler.post(new CompassWork(event, this));
				break;
			default:
				break;
		}
	}

	private abstract class SensorWork implements Runnable {

		protected SensorEvent event;
		SensorWork(SensorEvent e) {
			event = e;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}
