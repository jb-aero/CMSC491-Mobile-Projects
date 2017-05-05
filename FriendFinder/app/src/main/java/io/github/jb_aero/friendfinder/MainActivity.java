package io.github.jb_aero.friendfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	LocationManager locationManager;
	EditText username, password;
	Button login, register;
	Handler handler;
	LoginStringRunnable runnable;
	Location lastLoc;
	String[] input;
	InvokeWebservice service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		input = new String[2];
		login = (Button) findViewById(R.id.logButton);
		register = (Button) findViewById(R.id.toRegButt);
		username = (EditText) findViewById(R.id.logUser);
		password = (EditText) findViewById(R.id.logPass);
		handler = new Handler();
		runnable = new LoginStringRunnable();

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		login.setOnClickListener(this);
		register.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1337);
			return;
		}
		lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	@Override
	public void onClick(View v) {

		Intent intent;
		switch (v.getId())
		{
			case R.id.logButton:
				service = new InvokeWebservice("Login", handler, runnable);
				input[0] = "?username=" + username.getText().toString();
				input[1] = "&password=" + FFUtility.md5(password.getText().toString());
				service.execute(input);
				break;
			case R.id.toRegButt:
				intent = new Intent(this, RegisterActivity.class);
				intent.putExtra("location", lastLoc);
				startActivity(intent);
				break;
		}
	}

	private class LoginStringRunnable extends StringRunnable {

		@Override
		public void run() {

			//Toast.makeText(MainActivity.this, theString, Toast.LENGTH_SHORT).show();
			if ("SUCCESS".equals(theString.substring(0, 7))) {
				Intent intent = new Intent(MainActivity.this, MapsActivity.class);
				intent.putExtra("username", username.getText().toString());
				intent.putExtra("id", theString.substring(8));
				startActivity(intent);
			} else if ("INVALIDLOGIN".equals(theString)) {
				Toast.makeText(MainActivity.this, "The username and password combination is not valid.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(MainActivity.this, "An error has occured: " + theString, Toast.LENGTH_LONG).show();
			}
		}
	}
}
