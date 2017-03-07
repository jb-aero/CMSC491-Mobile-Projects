package io.github.jb_aero.averagerms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * MainActivity for in-class assignment 1
 *
 * @author James Bilbrey
 * @author Katelyn Seitz
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private MyService myService;
	private boolean bound;
	Button button;
	TextView target;
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bound = false;
		Intent intent = new Intent(this, MyService.class);
		bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

		button = (Button) findViewById(R.id.button);
		target = (TextView) findViewById(R.id.target);
	}

	private ServiceConnection myConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyService.MyBinder binder_ = (MyService.MyBinder) service;
			myService = binder_.getService();
			bound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};

	@Override
	public void onClick(View v) {

		if (v.getId() != R.id.button) {
			return;
		}

		handler.post(new Handling());
	}

	public class Handling implements Runnable {

		@Override
		public void run() {
			target.setText(String.valueOf(myService.pollAverage()));
		}
	}
}
