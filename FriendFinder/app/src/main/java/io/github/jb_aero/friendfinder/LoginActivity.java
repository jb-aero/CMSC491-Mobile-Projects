package io.github.jb_aero.friendfinder;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

	Handler handler = new Handler();
	TextView retext, urltext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		urltext = (TextView) findViewById(R.id.urltext);
		retext = (TextView) findViewById(R.id.response);

		String latitude = "49.5";
		String longitude = "-72.5";
		int id = 1337;

		String[] input = new String[4];
		input[0] = latitude;
		input[1] = longitude;
		input[2] = String.valueOf(id);

		InvokeWebservice mywebservice = new InvokeWebservice();
		mywebservice.execute(input);
	}

	private class InvokeWebservice extends AsyncTask<String,Integer,String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(final String s) {
			super.onPostExecute(s);
			handler.post(new Runnable() {
				@Override
				public void run() {
					retext.setText(s);
				}
			});
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(String... params) {

			URL url;
			int responseCode = -1337;
			String response = "";

			StringBuilder str = new StringBuilder("http://192.168.1.151/uploaddata.php");
			str.append("?id="+params[2]);
			str.append("&latitude="+params[0]).append("&longitude="+params[1]);
			final String requestURL = str.toString();

			try
			{
				url = new URL(requestURL);
				HttpURLConnection myconnection =  (HttpURLConnection) url.openConnection();
				myconnection.setReadTimeout(15000);
				myconnection.setConnectTimeout(15000);
				myconnection.setRequestMethod("GET");
				myconnection.setDoInput(true);
				myconnection.setDoOutput(true);

				handler.post(new Runnable() {
					@Override
					public void run() {
						urltext.setText("URL: " + requestURL);
					}
				});

				responseCode = myconnection.getResponseCode();

				if(responseCode == HttpURLConnection.HTTP_OK)
				{
					String line;
					BufferedReader br = new BufferedReader(new InputStreamReader(
							myconnection.getInputStream()));

					line = br.readLine();
					while(line != null)
					{
						response += line;
						line = br.readLine();
					}
					br.close();
				}
				myconnection.disconnect();
				Log.d("HEYLOOK", response);

			}catch(Exception e)
			{
				e.printStackTrace();
			}

			return responseCode + ": " + response;
		}
	}
}
