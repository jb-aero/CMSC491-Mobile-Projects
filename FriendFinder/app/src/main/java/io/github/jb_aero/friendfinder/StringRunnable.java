package io.github.jb_aero.friendfinder;

/**
 * Intended to be extended with the extending class executed in an onPostExecute
 */
public abstract class StringRunnable implements Runnable {

	protected String theString;

	public void setString(String newString) {
		theString = newString;
	}
}
