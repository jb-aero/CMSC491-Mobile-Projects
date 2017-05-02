package io.github.jb_aero.friendfinder;

import android.location.Location;
import android.location.LocationManager;

import java.util.Random;

/**
 * I wrote this because I'm sick and tired of having to run outside and hope my garbage
 * cellular carrier is feeling cooperative every time I want to test a location listener.
 */
public class LocationSpoof {

	// 39.251962, -76.716703 Southwest corner of UMBC
	// 39.259512, -76.705731 Northeast corner of UMBC

	private static Random random = new Random();

	private static final double MINLAT = 39.251962;
	private static final double DELTALAT = 39.259512 - MINLAT;

	private static final double MINLON = -76.716703;
	private static final double DELTALON = -76.705731 - MINLON;

	public static Location spoof() {
		double p1 = random.nextDouble();
		while (p1 > DELTALAT) {
			p1 /= 10;
		}
		p1 += MINLAT;

		double p2 = random.nextDouble();
		while (p2 > DELTALON) {
			p2 /= 10;
		}
		p2 += MINLON;

		Location ret = new Location(LocationManager.GPS_PROVIDER);
		ret.setLatitude(p1);
		ret.setLongitude(p2);
		ret.setTime(System.currentTimeMillis());

		return ret;
	}
}
