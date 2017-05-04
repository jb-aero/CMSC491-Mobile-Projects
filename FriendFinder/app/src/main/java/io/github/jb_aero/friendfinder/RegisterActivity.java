package io.github.jb_aero.friendfinder;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

	EditText username, password;
	Button register;
	Handler handler;
	InvokeWebservice service;
	RegisterStringRunnable runnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		handler = new Handler();
		runnable = new RegisterStringRunnable();
		register = (Button) findViewById(R.id.regButton);
		username = (EditText) findViewById(R.id.regUser);
		password = (EditText) findViewById(R.id.regPass);
		register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.regButton:
				service = new InvokeWebservice("Register", handler, runnable);
				break;
		}
	}

	private class RegisterStringRunnable extends StringRunnable {

		@Override
		public void run() {

		}
	}
}
