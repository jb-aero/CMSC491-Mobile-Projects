package io.github.jb_aero.friendfinder;

import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class InvokeWebservice extends AsyncTask<String,Integer,String> {

	String phpfile;
	Handler handler;
	StringRunnable runnable;
	public InvokeWebservice(String phpfile, Handler handler, StringRunnable runnable)
	{
		this.phpfile = phpfile;
		this.handler = handler;
		this.runnable = runnable;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(final String s) {
		super.onPostExecute(s);
		try {
			runnable.setString(s);
			handler.post(runnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		StringBuilder str = new StringBuilder("http://REDACTED/" + phpfile + ".php");
		for (String s : params) {
			str.append(s);
		}
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

		}catch(Exception e)
		{
			e.printStackTrace();
		}

		return response;
	}
}
