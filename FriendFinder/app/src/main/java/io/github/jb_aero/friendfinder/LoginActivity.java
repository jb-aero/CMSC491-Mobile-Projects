package io.github.jb_aero.friendfinder;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

	Handler handler;
	TextView retext, urltext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		urltext = (TextView) findViewById(R.id.urltext);
		retext = (TextView) findViewById(R.id.response);
		handler = new Handler();

		String latitude = "49.5";
		String longitude = "-72.5";
		int id = 1337;

		String[] input = new String[3];
		input[0] = "?id=" + id;
		input[1] = "&latitude=" + latitude;
		input[2] = "&longitude=" + longitude;

		InvokeWebservice mywebservice = new InvokeWebservice("uploaddata", handler, new LoginStringRunnable());
		mywebservice.execute(input);
	}

	private class LoginStringRunnable extends StringRunnable {

		@Override
		public void run() {
			retext.setText(theString);
		}
	}
}
