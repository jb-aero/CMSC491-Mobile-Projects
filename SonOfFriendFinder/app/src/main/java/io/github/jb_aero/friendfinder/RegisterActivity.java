package io.github.jb_aero.friendfinder;

import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

	FirebaseAuth auth;
	DatabaseReference data;
	EditText username, password;
	Button register;
	Handler handler;
	InvokeWebservice service;
	RegisterStringRunnable runnable;
	String[] input;
	Location loc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		loc = getIntent().getParcelableExtra("location");
		input = new String[4];
		handler = new Handler();
		runnable = new RegisterStringRunnable();

		auth = FirebaseAuth.getInstance();
		data = FirebaseDatabase.getInstance().getReference();

		register = (Button) findViewById(R.id.regButton);
		username = (EditText) findViewById(R.id.regUser);
		password = (EditText) findViewById(R.id.regPass);
		register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.regButton:
				if (password.getText().toString().length() < 6) {
					Toast.makeText(this, "Please use a password of 6 characters or longer.", Toast.LENGTH_LONG).show();
					return;
				}

				auth.createUserWithEmailAndPassword(username.getText().toString(),
						FFUtility.md5(password.getText().toString()))
						.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
							@Override
							public void onComplete(@NonNull Task<AuthResult> task) {
								if (task.isSuccessful()) {
									FFUtility.signIn(RegisterActivity.this, email);
								} else {
									Toast.makeText(RegisterActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
								}
							}
						});

				service = new InvokeWebservice("Register", handler, runnable);
				input[0] = "?username=" + username.getText().toString();
				// This will be insecure because we aren't using an encrypted connection
				// If this was legit, we'd have legitimate certificates for HTTPS and be
				// using POST instead of GET
				input[1] = "&password=" + FFUtility.md5(password.getText().toString());
				input[2] = "&latitude=" + loc.getLatitude();
				input[3] = "&longitude=" + loc.getLongitude();
				service.execute(input);
				break;
		}
	}

	private class RegisterStringRunnable extends StringRunnable {

		@Override
		public void run() {

			if ("SUCCESSFUL".equals(theString)) {
				Toast.makeText(RegisterActivity.this, "Your account has been created, please sign in to use it.", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
				startActivity(intent);
			} else if ("ALREADYEXISTS".equals(theString)) {
				Toast.makeText(RegisterActivity.this, "The username entered is already in use, please use a different one.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(RegisterActivity.this, "An error has occured: " + theString, Toast.LENGTH_LONG).show();
			}
		}
	}
}
