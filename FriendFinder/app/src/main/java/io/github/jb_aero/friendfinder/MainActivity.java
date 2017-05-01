package io.github.jb_aero.friendfinder;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	Button php, map;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		php = (Button) findViewById(R.id.phpbutton);
		map = (Button) findViewById(R.id.mapbutton);

		php.setOnClickListener(this);
		map.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		Intent intent;
		switch (v.getId())
		{
			case R.id.phpbutton:
				intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				break;
			case R.id.mapbutton:
				intent = new Intent(this, MapsActivity.class);
				startActivity(intent);
				break;
		}
	}
}
