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
	TextView retext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		retext = (TextView) findViewById(R.id.textView);

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
			String requestURL = "https://REDACTED/uploaddata.php";
			try
			{
				url = new URL(requestURL);
				HttpURLConnection myconnection =  (HttpURLConnection) url.openConnection();
				myconnection.setReadTimeout(15000);
				myconnection.setConnectTimeout(15000);
				myconnection.setRequestMethod("GET");
				myconnection.setDoInput(true);
				myconnection.setDoOutput(true);

				OutputStream os =  myconnection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
				StringBuilder str = new StringBuilder(requestURL);

				str.append("?id="+params[2]);
				str.append("&latitude="+params[0]).append("&longitude="+params[1]);

				final String urstr = str.toString();

				handler.post(new Runnable() {
					@Override
					public void run() {
						retext.setText("URL: " + urstr);
					}
				});

				writer.write(urstr);
				writer.flush();
				writer.close();

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
